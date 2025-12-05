package com.movesmart.demo.controller

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

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val busService: BusService
) {

    @PostMapping
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
    fun getAllUsers(): ResponseEntity<List<User>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users)
    }

    @GetMapping("/drivers")
    fun getDrivers(): ResponseEntity<List<User>> {
        val drivers = userService.getUsersByRole(UserRole.DRIVER)
        return ResponseEntity.ok(drivers)
    }

    @GetMapping("/passengers")
    fun getPassengers(): ResponseEntity<List<User>> {
        val passengers = userService.getUsersByRole(UserRole.PASSENGER)
        return ResponseEntity.ok(passengers)
    }

    @GetMapping("/admin")
    fun getAdmins(): ResponseEntity<List<User>> {
        val admins = userService.getUsersByRole(UserRole.ADMIN)
        return ResponseEntity.ok(admins)
    }

    @GetMapping("/me")
    fun getCurrentUser(authentication: Authentication): ResponseEntity<Any> {
        return try {
            val username = authentication.name
            val user = userService.findByEmailOrPhone(username)
            ResponseEntity.ok(user)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("message" to (ex.message ?: "User not found")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("message" to "An error occurred"))
        }
    }
    
    @GetMapping("/{id}")
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
    
    @PutMapping("/{id}")
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