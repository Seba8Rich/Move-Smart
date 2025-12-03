package com.movesmart.demo.controller

import com.movesmart.demo.model.User
import com.movesmart.demo.model.UserRole
import com.movesmart.demo.security.JwtService
import com.movesmart.demo.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*

data class LoginRequest(val userEmail: String, val userPassword: String)
data class AuthResponse(val token: String)
data class RegisterRequest(
    val userName: String,
    val userEmail: String,
    val userPhoneNumber: String,
    val userPassword: String,
    val userRole: String? = null // Optional - will be set by endpoint if not provided
)

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val userService: UserService
) {

    /**
     * Main registration endpoint - accepts userRole in request body
     * Note: ADMIN role registration is not allowed here - use /api/auth/register/admin instead
     * POST /api/auth/register
     * Body: { "userName": "...", "userEmail": "...", "userPhoneNumber": "...", "userPassword": "...", "userRole": "USER" }
     */
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<Any> {
        return try {
            // Validate required fields
            val validationErrors = mutableListOf<String>()

            if (request.userName.isBlank()) {
                validationErrors.add("userName is required and cannot be empty")
            }
            if (request.userPhoneNumber.isBlank()) {
                validationErrors.add("userPhoneNumber is required and cannot be empty")
            }
            if (request.userPassword.isBlank()) {
                validationErrors.add("userPassword is required and cannot be empty")
            }
            if (request.userPassword.length < 6) {
                validationErrors.add("userPassword must be at least 6 characters long")
            }

            if (validationErrors.isNotEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf(
                        "error" to "Bad Request",
                        "message" to "Validation failed: ${validationErrors.joinToString(", ")}",
                        "errors" to validationErrors,
                        "receivedFields" to mapOf(
                            "userName" to (request.userName.take(10) + if (request.userName.length > 10) "..." else ""),
                            "userEmail" to (if (request.userEmail.isNotBlank()) "provided" else "empty"),
                            "userPhoneNumber" to (request.userPhoneNumber.take(10) + if (request.userPhoneNumber.length > 10) "..." else ""),
                            "userPasswordLength" to request.userPassword.length
                        )
                    ))
            }

            // Prevent ADMIN registration through main endpoint
            if (!request.userRole.isNullOrBlank() && request.userRole.uppercase() == "ADMIN") {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf(
                        "error" to "Bad Request",
                        "message" to "ADMIN registration is not allowed through this endpoint. Use /api/auth/register/admin instead."
                    ))
            }

            val userRole = if (!request.userRole.isNullOrBlank()) {
                try {
                    UserRole.valueOf(request.userRole.uppercase())
                } catch (e: IllegalArgumentException) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(mapOf("error" to "Bad Request", "message" to "Invalid user role: ${request.userRole}. Valid roles: USER, PASSENGER, DRIVER. For ADMIN, use /api/auth/register/admin"))
                }
            } else {
                UserRole.USER // Default role
            }

            val user = User(
                userName = request.userName.trim(),
                userEmail = request.userEmail.trim().takeIf { it.isNotBlank() } ?: "",
                userPhoneNumber = request.userPhoneNumber.trim(),
                userPassword = request.userPassword,
                userRole = userRole
            )

            val saved = userService.createUser(user)
            val principal = saved.userEmail.ifBlank { saved.userPhoneNumber }
            val token = jwtService.generateToken(principal, mapOf("role" to saved.userRole.name))
            return ResponseEntity.ok(AuthResponse(token))
        } catch (ex: IllegalArgumentException) {
            // Handle validation errors from UserService (e.g., email/phone already exists)
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Bad Request", "message" to (ex.message ?: "Registration failed")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf(
                    "error" to "Bad Request",
                    "message" to (ex.message ?: "Registration failed"),
                    "exceptionType" to ex.javaClass.simpleName
                ))
        }
    }

    /**
     * Convenience endpoint for passenger registration
     * POST /api/auth/register/passenger
     */
    @PostMapping("/register/passenger")
    fun registerPassenger(@RequestBody request: RegisterRequest): ResponseEntity<Any> {
        val userWithRole = request.copy(userRole = "PASSENGER")
        return register(userWithRole)
    }

    /**
     * Convenience endpoint for driver registration
     * POST /api/auth/register/driver
     */
    @PostMapping("/register/driver")
    fun registerDriver(@RequestBody request: RegisterRequest): ResponseEntity<Any> {
        val userWithRole = request.copy(userRole = "DRIVER")
        return register(userWithRole)
    }

    /**
     * Convenience endpoint for regular user registration
     * POST /api/auth/register/user
     */
    @PostMapping("/register/user")
    fun registerUser(@RequestBody request: RegisterRequest): ResponseEntity<Any> {
        val userWithRole = request.copy(userRole = "USER")
        return register(userWithRole)
    }

    /**
     * Convenience endpoint for admin registration (should be protected in production)
     * POST /api/auth/register/admin
     */
    @PostMapping("/register/admin")
    fun registerAdmin(@RequestBody request: RegisterRequest): ResponseEntity<Any> {
        return try {
            // Validate required fields
            val validationErrors = mutableListOf<String>()

            if (request.userName.isBlank()) {
                validationErrors.add("userName is required and cannot be empty")
            }
            if (request.userPhoneNumber.isBlank()) {
                validationErrors.add("userPhoneNumber is required and cannot be empty")
            }
            if (request.userPassword.isBlank()) {
                validationErrors.add("userPassword is required and cannot be empty")
            }
            if (request.userPassword.length < 6) {
                validationErrors.add("userPassword must be at least 6 characters long")
            }

            if (validationErrors.isNotEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf(
                        "error" to "Bad Request",
                        "message" to "Validation failed: ${validationErrors.joinToString(", ")}",
                        "errors" to validationErrors
                    ))
            }

            // Allow ADMIN role registration through this endpoint
            val userRole = UserRole.ADMIN

            val user = User(
                userName = request.userName.trim(),
                userEmail = request.userEmail.trim().takeIf { it.isNotBlank() } ?: "",
                userPhoneNumber = request.userPhoneNumber.trim(),
                userPassword = request.userPassword,
                userRole = userRole
            )

            val saved = userService.createUser(user)
            val principal = saved.userEmail.ifBlank { saved.userPhoneNumber }
            val token = jwtService.generateToken(principal, mapOf("role" to saved.userRole.name))
            return ResponseEntity.ok(AuthResponse(token))
        } catch (ex: IllegalArgumentException) {
            // Handle validation errors from UserService (e.g., email/phone already exists)
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Bad Request", "message" to (ex.message ?: "Registration failed")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf(
                    "error" to "Bad Request",
                    "message" to (ex.message ?: "Registration failed"),
                    "exceptionType" to ex.javaClass.simpleName
                ))
        }
    }

    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): ResponseEntity<Any> {
        return try {
            // Validate input
            if (req.userEmail.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to "Bad Request", "message" to "userEmail is required"))
            }
            if (req.userPassword.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to "Bad Request", "message" to "password is required"))
            }

            val authToken = UsernamePasswordAuthenticationToken(req.userEmail.trim(), req.userPassword)
            val auth = authenticationManager.authenticate(authToken)
            val principal = auth.name

            val user = userService.findByEmailOrPhone(principal)
            val token = jwtService.generateToken(principal, mapOf("role" to user.userRole.name))

            ResponseEntity.ok(AuthResponse(token))
        } catch (ex: AuthenticationException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Unauthorized", "message" to "Invalid credentials"))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Bad Request", "message" to (ex.message ?: "Invalid request")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Internal Server Error", "message" to (ex.message ?: "An error occurred")))
        }
    }

    data class ChangePasswordRequest(
        val oldPassword: String,
        val newPassword: String
    )

    @PostMapping("/change-password")
    fun changePassword(
        @RequestBody request: ChangePasswordRequest,
        authentication: org.springframework.security.core.Authentication
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

            val userEmail = authentication.name
            val user = userService.findByEmailOrPhone(userEmail)

            if (user.userId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(mapOf("error" to "Bad Request", "message" to "User ID is missing"))
            }

            userService.changePassword(user.userId!!, request.oldPassword, request.newPassword)
            ResponseEntity.ok(mapOf("message" to "Password changed successfully"))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Bad Request", "message" to (ex.message ?: "Failed to change password")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Internal Server Error", "message" to (ex.message ?: "An error occurred")))
        }
    }

    @PostMapping("/logout")
    fun logout(authentication: org.springframework.security.core.Authentication?): ResponseEntity<Any> {
        // With JWT stateless authentication, logout is handled client-side by removing the token
        // This endpoint is provided for consistency and can be used for logging purposes
        // The authentication parameter is optional to allow logging logout events
        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
    }
}

