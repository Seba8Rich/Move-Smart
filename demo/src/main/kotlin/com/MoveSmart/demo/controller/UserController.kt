package com.movesmart.demo.controller

import com.movesmart.demo.dto.AssignedBusInfo
import com.movesmart.demo.dto.DriverInfo
import com.movesmart.demo.dto.DriverProfileResponse
import com.movesmart.demo.dto.RouteInfo
import com.movesmart.demo.model.User
import com.movesmart.demo.model.UserRole
import com.movesmart.demo.service.BusService
import com.movesmart.demo.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

data class UpdateProfileRequest(
    val userName: String? = null,
    val userEmail: String? = null,
    val userPhoneNumber: String? = null
)

data class UpdateUserRequest(
    val userName: String? = null,
    val userEmail: String? = null,
    val userPhoneNumber: String? = null,
    val userPassword: String? = null,
    val userRole: String? = null
)

data class AssignBusRequest(
    val busId: Long
)

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val busService: BusService
) {

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    fun createUser(@RequestBody user: User): ResponseEntity<Any> {
        return try {
            val createdUser = userService.createUser(user)
            ResponseEntity.status(HttpStatus.CREATED).body(createdUser)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to (ex.message ?: "Failed to create user")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("message" to "An error occurred while creating user"))
        }
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
    fun getCurrentUser(authentication: Authentication): ResponseEntity<Any> {
        return try {
            val username = authentication.name
            val user = userService.findByEmailOrPhone(username)
            
            // If user is a driver, return driver profile with bus info
            if (user.userRole == UserRole.DRIVER) {
                val buses = busService.getBusesByDriverId(user.userId ?: throw IllegalArgumentException("Driver ID not found"))
                val assignedBus = if (buses.isNotEmpty()) {
                    val bus = buses.first()
                    val (busWithDetails, route) = busService.getBusWithRouteInfo(bus.id)
                    
                    // If route entity exists, use it; otherwise parse route string
                    val routeInfo = route?.let {
                        RouteInfo(
                            id = it.id,
                            routeId = it.routeId,
                            routeName = "${it.startStation} - ${it.endStation}",
                            startStation = it.startStation,
                            endStation = it.endStation,
                            distanceKm = it.distanceKm
                        )
                    } ?: if (busWithDetails.route.isNotBlank()) {
                        // Parse route string (format: "StartStation to EndStation")
                        val routeParts = busWithDetails.route.split(" to ", limit = 2)
                        if (routeParts.size == 2) {
                            RouteInfo(
                                id = 0,
                                routeId = null,
                                routeName = busWithDetails.route,
                                startStation = routeParts[0].trim(),
                                endStation = routeParts[1].trim(),
                                distanceKm = 0.0
                            )
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                    
                    AssignedBusInfo(
                        busId = busWithDetails.id,
                        plateNumber = busWithDetails.plateNumber,
                        capacity = busWithDetails.capacity,
                        route = routeInfo
                    )
                } else {
                    null
                }
                
                val driverProfile = DriverProfileResponse(
                    driver = DriverInfo(
                        userId = user.userId ?: 0,
                        userName = user.userName,
                        userEmail = user.userEmail,
                        userPhoneNumber = user.userPhoneNumber,
                        userRole = user.userRole.name
                    ),
                    assignedBus = assignedBus
                )
                return ResponseEntity.ok(driverProfile)
            }
            
            // For non-drivers, return regular user info
            ResponseEntity.ok(user)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to (ex.message ?: "User not found")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("message" to "An error occurred"))
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    fun getUserById(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            val user = userService.getUserById(id)
            ResponseEntity.ok(user)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to (ex.message ?: "User not found")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("message" to "An error occurred"))
        }
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun updateCurrentUser(
        @RequestBody request: UpdateProfileRequest,
        authentication: Authentication
    ): ResponseEntity<Any> {
        return try {
            val username = authentication.name
            val updatedUser = userService.updateCurrentUserProfile(
                username,
                request.userName,
                request.userEmail,
                request.userPhoneNumber
            )
            ResponseEntity.ok(updatedUser)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to (ex.message ?: "Failed to update profile")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("message" to "An error occurred"))
        }
    }
    
    @PutMapping("/{id}/password")
    fun changePassword(
        @PathVariable id: Long,
        @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<Any> {
        return try {
            // Validate input
            if (request.oldPassword.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to "Bad Request", "message" to "oldPassword is required"))
            }
            if (request.newPassword.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to "Bad Request", "message" to "newPassword is required"))
            }
            if (request.newPassword.length < 6) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to "Bad Request", "message" to "newPassword must be at least 6 characters long"))
            }

            userService.changePassword(id, request.oldPassword, request.newPassword)
            ResponseEntity.ok(mapOf("message" to "Password changed successfully"))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Bad Request", "message" to (ex.message ?: "Failed to change password")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Internal Server Error", "message" to (ex.message ?: "An error occurred")))
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    fun updateUser(@PathVariable id: Long, @RequestBody request: UpdateUserRequest): ResponseEntity<Any> {
        return try {
            val updatedUser = userService.updateUser(
                id,
                request.userName,
                request.userEmail,
                request.userPhoneNumber,
                request.userPassword,
                request.userRole
            )
            ResponseEntity.ok(updatedUser)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to (ex.message ?: "Failed to update user")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("message" to "An error occurred while updating user"))
        }
    }

    @PutMapping("/drivers/{driverId}/assign-bus")
    @PreAuthorize("hasRole('ADMIN')")
    fun assignBusToDriver(
        @PathVariable driverId: Long,
        @RequestBody request: AssignBusRequest
    ): ResponseEntity<Any> {
        return try {
            // Verify the user exists and is a driver
            val driver = userService.getUserById(driverId)
            
            if (driver.userRole != UserRole.DRIVER) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("message" to "User is not a driver. Only drivers can be assigned to buses."))
            }
            
            // Check if busId = 0, which is a signal to unassign the driver
            if (request.busId == 0L) {
                // Unassign the driver from all buses
                if (!busService.isDriverAssignedToBus(driverId)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(mapOf("message" to "Driver is not assigned to any bus."))
                }
                
                val updatedRows = busService.unassignDriverFromBus(driverId)
                return ResponseEntity.ok(mapOf(
                    "message" to "Driver unassigned from bus successfully",
                    "busesUnassigned" to updatedRows
                ))
            }
            
            // Assign the bus to the driver
            val updatedBus = busService.updateBusWithDriver(request.busId, driver)
            
            ResponseEntity.ok(mapOf(
                "message" to "Bus assigned to driver successfully",
                "bus" to updatedBus
            ))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to (ex.message ?: "Failed to assign bus to driver")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("message" to "An error occurred while assigning bus to driver"))
        }
    }

    @PutMapping("/drivers/{driverId}/unassign-bus")
    @PreAuthorize("hasRole('ADMIN')")
    fun unassignBusFromDriver(
        @PathVariable driverId: Long
    ): ResponseEntity<Any> {
        return try {
            // Verify the user exists and is a driver
            val driver = userService.getUserById(driverId)
            
            if (driver.userRole != UserRole.DRIVER) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("message" to "User is not a driver. Only drivers can be unassigned from buses."))
            }
            
            // Check if driver is assigned to any bus
            if (!busService.isDriverAssignedToBus(driverId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("message" to "Driver is not assigned to any bus."))
            }
            
            // Unassign the driver from all buses
            val updatedRows = busService.unassignDriverFromBus(driverId)
            
            ResponseEntity.ok(mapOf(
                "message" to "Driver unassigned from bus successfully",
                "busesUnassigned" to updatedRows
            ))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to (ex.message ?: "Failed to unassign bus from driver")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("message" to "An error occurred while unassigning bus from driver"))
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            if (userService.deleteUser(id)) {
                ResponseEntity.ok(mapOf("message" to "User deleted successfully"))
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to "User not found"))
            }
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("message" to "An error occurred while deleting user"))
        }
    }
}