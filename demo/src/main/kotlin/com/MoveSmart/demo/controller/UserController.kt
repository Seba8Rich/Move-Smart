package com.movesmart.demo.controller

import com.movesmart.demo.dto.*
import com.movesmart.demo.model.Bus
import com.movesmart.demo.model.Route
import com.movesmart.demo.model.User
import com.movesmart.demo.model.UserRole
import com.movesmart.demo.service.BusService
import com.movesmart.demo.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val busService: BusService
) {

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        val createdUser = userService.createUser(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser)
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    fun getAllUsers(): ResponseEntity<List<User>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users)
    }

    @GetMapping("/drivers")
    @PreAuthorize("hasRole('ADMIN')")
    fun getDrivers(): ResponseEntity<List<User>> {
        val drivers = userService.getUsersByRole(UserRole.DRIVER)
        return ResponseEntity.ok(drivers)
    }

    @GetMapping("/passengers")
    @PreAuthorize("hasRole('ADMIN')")
    fun getPassengers(): ResponseEntity<List<User>> {
        val passengers = userService.getUsersByRole(UserRole.PASSENGER)
        return ResponseEntity.ok(passengers)
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    fun getAdmins(): ResponseEntity<List<User>> {
        val admins = userService.getUsersByRole(UserRole.ADMIN)
        return ResponseEntity.ok(admins)
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun getCurrentUser(authentication: Authentication): ResponseEntity<Any> {
        val user = getCurrentUserFromAuth(authentication)
        
        // If user is a driver, return driver profile with bus info
        if (user.userRole == UserRole.DRIVER) {
            val driverProfile = buildDriverProfile(user)
            return ResponseEntity.ok(driverProfile)
        }
        
        // For non-drivers, return regular user info
        return ResponseEntity.ok(user)
    }
    
    private fun buildDriverProfile(user: User): DriverProfileResponse {
        val driverId = user.userId ?: throw IllegalArgumentException("Driver ID not found")
        val buses = busService.getBusesByDriverId(driverId)
        val assignedBus = buses.firstOrNull()?.let { buildAssignedBusInfo(it) }
        
        return DriverProfileResponse(
            driver = buildDriverInfo(user),
            assignedBus = assignedBus
        )
    }
    
    private fun buildDriverInfo(user: User): DriverInfo {
        return DriverInfo(
            userId = user.userId ?: 0,
            userName = user.userName,
            userEmail = user.userEmail,
            userPhoneNumber = user.userPhoneNumber,
            userRole = user.userRole.name
        )
    }
    
    private fun buildAssignedBusInfo(bus: Bus): AssignedBusInfo {
        val (busWithDetails, route) = busService.getBusWithRouteInfo(bus.id)
        val routeInfo = buildRouteInfo(route, busWithDetails.route)
        
        return AssignedBusInfo(
            busId = busWithDetails.id,
            plateNumber = busWithDetails.plateNumber,
            capacity = busWithDetails.capacity,
            route = routeInfo
        )
    }
    
    private fun buildRouteInfo(route: Route?, routeString: String): RouteInfo? {
        // If route entity exists, use it
        route?.let {
            return RouteInfo(
                id = it.id,
                routeId = it.routeId,
                routeName = "${it.startStation} - ${it.endStation}",
                startStation = it.startStation,
                endStation = it.endStation,
                distanceKm = it.distanceKm
            )
        }
        
        // Otherwise, parse route string (format: "StartStation to EndStation")
        if (routeString.isNotBlank()) {
            val routeParts = routeString.split(" to ", limit = 2)
            if (routeParts.size == 2) {
                return RouteInfo(
                    id = 0,
                    routeId = null,
                    routeName = routeString,
                    startStation = routeParts[0].trim(),
                    endStation = routeParts[1].trim(),
                    distanceKm = 0.0
                )
            }
        }
        
        return null
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    fun getUserById(@PathVariable id: Long): ResponseEntity<User> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(user)
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun updateCurrentUser(
        @RequestBody request: UpdateProfileRequest,
        authentication: Authentication
    ): ResponseEntity<User> {
        val username = authentication.name
        val updatedUser = userService.updateCurrentUserProfile(
            username,
            request.userName,
            request.userEmail,
            request.userPhoneNumber
        )
        return ResponseEntity.ok(updatedUser)
    }
    
    @PutMapping("/{id}/password")
    @PreAuthorize("isAuthenticated()")
    fun changePassword(
        @PathVariable id: Long,
        @RequestBody request: ChangePasswordRequest,
        authentication: Authentication
    ): ResponseEntity<Map<String, String>> {
        // Security check: user can only change their own password unless they're an admin
        val currentUser = getCurrentUserFromAuth(authentication)
        if (currentUser.userId != id && currentUser.userRole != UserRole.ADMIN) {
            throw IllegalArgumentException("You can only change your own password")
        }

        // Validation is handled by the service layer
        userService.changePassword(id, request.oldPassword, request.newPassword)
        return ResponseEntity.ok(mapOf("message" to "Password changed successfully"))
    }
    
    private fun getCurrentUserFromAuth(authentication: Authentication): User {
        val username = authentication.name
        return userService.findByEmailOrPhone(username)
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    fun updateUser(@PathVariable id: Long, @RequestBody request: UpdateUserRequest): ResponseEntity<User> {
        val updatedUser = userService.updateUser(
            id,
            request.userName,
            request.userEmail,
            request.userPhoneNumber,
            request.userPassword,
            request.userRole
        )
        return ResponseEntity.ok(updatedUser)
    }

    @PutMapping("/drivers/{driverId}/assign-bus")
    @PreAuthorize("hasRole('ADMIN')")
    fun assignBusToDriver(
        @PathVariable driverId: Long,
        @RequestBody request: AssignBusRequest
    ): ResponseEntity<Map<String, Any>> {
        // Verify the user exists and is a driver
        val driver = userService.getUserById(driverId)
        
        if (driver.userRole != UserRole.DRIVER) {
            throw IllegalArgumentException("User is not a driver. Only drivers can be assigned to buses.")
        }
        
        // Check if busId = 0, which is a signal to unassign the driver
        if (request.busId == 0L) {
            if (!busService.isDriverAssignedToBus(driverId)) {
                throw IllegalArgumentException("Driver is not assigned to any bus.")
            }
            
            val updatedRows = busService.unassignDriverFromBus(driverId)
            return ResponseEntity.ok(mapOf(
                "message" to "Driver unassigned from bus successfully",
                "busesUnassigned" to updatedRows
            ))
        }
        
        // Assign the bus to the driver
        val updatedBus = busService.updateBusWithDriver(request.busId, driver)
        
        return ResponseEntity.ok(mapOf(
            "message" to "Bus assigned to driver successfully",
            "bus" to updatedBus
        ))
    }

    @PutMapping("/drivers/{driverId}/unassign-bus")
    @PreAuthorize("hasRole('ADMIN')")
    fun unassignBusFromDriver(
        @PathVariable driverId: Long
    ): ResponseEntity<Map<String, Any>> {
        // Verify the user exists and is a driver
        val driver = userService.getUserById(driverId)
        
        if (driver.userRole != UserRole.DRIVER) {
            throw IllegalArgumentException("User is not a driver. Only drivers can be unassigned from buses.")
        }
        
        // Check if driver is assigned to any bus
        if (!busService.isDriverAssignedToBus(driverId)) {
            throw IllegalArgumentException("Driver is not assigned to any bus.")
        }
        
        // Unassign the driver from all buses
        val updatedRows = busService.unassignDriverFromBus(driverId)
        
        return ResponseEntity.ok(mapOf(
            "message" to "Driver unassigned from bus successfully",
            "busesUnassigned" to updatedRows
        ))
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Map<String, String>> {
        if (userService.deleteUser(id)) {
            return ResponseEntity.ok(mapOf("message" to "User deleted successfully"))
        }
        throw IllegalArgumentException("User not found with ID: $id")
    }
}