package com.movesmart.demo.controller

import com.movesmart.demo.dto.*
import com.movesmart.demo.model.User
import com.movesmart.demo.model.UserRole
import com.movesmart.demo.security.JwtService
import com.movesmart.demo.service.UserService
import com.movesmart.demo.service.BusService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val userService: UserService,
    private val busService: BusService
) {

    private fun validateRegisterRequest(request: RegisterRequest): List<String> {
        val errors = mutableListOf<String>()
        
        if (request.userName.isBlank()) {
            errors.add("userName is required and cannot be empty")
        }
        if (request.userPhoneNumber.isBlank()) {
            errors.add("userPhoneNumber is required and cannot be empty")
        }
        if (request.userPassword.isBlank()) {
            errors.add("userPassword is required and cannot be empty")
        } else if (request.userPassword.length < 6) {
            errors.add("userPassword must be at least 6 characters long")
        }
        
        return errors
    }

    private fun validateDriverRequest(request: RegisterDriverRequest): List<String> {
        val errors = validateRegisterRequest(
            RegisterRequest(
                userName = request.userName,
                userEmail = request.userEmail,
                userPhoneNumber = request.userPhoneNumber,
                userPassword = request.userPassword
            )
        ).toMutableList()
        
        if (request.busPlateNumber.isBlank()) {
            errors.add("busPlateNumber is required and cannot be empty")
        }
        
        return errors
    }

    private fun createUserFromRequest(
        request: RegisterRequest,
        userRole: UserRole
    ): User {
        return User(
            userName = request.userName.trim(),
            userEmail = request.userEmail.trim().takeIf { it.isNotBlank() } ?: "",
            userPhoneNumber = request.userPhoneNumber.trim(),
            userPassword = request.userPassword,
            userRole = userRole
        )
    }

    private fun generateAuthToken(user: User): AuthResponse {
        val principal = user.userEmail.ifBlank { user.userPhoneNumber }
        val token = jwtService.generateToken(principal, mapOf("role" to user.userRole.name))
        return AuthResponse(token)
    }

    /**
     * Main registration endpoint - accepts userRole in request body
     * Note: ADMIN role registration is not allowed here - use /api/auth/register/admin instead
     * POST /api/auth/register
     * Body: { "userName": "...", "userEmail": "...", "userPhoneNumber": "...", "userPassword": "...", "userRole": "USER" }
     */
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        // Validate required fields
        val validationErrors = validateRegisterRequest(request)
        if (validationErrors.isNotEmpty()) {
            throw IllegalArgumentException("Validation failed: ${validationErrors.joinToString(", ")}")
        }

        // Prevent ADMIN registration through main endpoint
        if (!request.userRole.isNullOrBlank() && request.userRole.uppercase() == "ADMIN") {
            throw IllegalArgumentException("ADMIN registration is not allowed through this endpoint. Use /api/auth/register/admin instead.")
        }

        val userRole = parseUserRole(request.userRole) ?: UserRole.USER
        val user = createUserFromRequest(request, userRole)
        val savedUser = userService.createUser(user)
        
        return ResponseEntity.ok(generateAuthToken(savedUser))
    }

    private fun parseUserRole(roleString: String?): UserRole? {
        if (roleString.isNullOrBlank()) return null
        
        return try {
            UserRole.valueOf(roleString.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid user role: $roleString. Valid roles: ${UserRole.values().joinToString { it.name }}")
        }
    }

    /**
     * Convenience endpoint for passenger registration
     * POST /api/auth/register/passenger
     */
    @PostMapping("/register/passenger")
    fun registerPassenger(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        val userWithRole = request.copy(userRole = "PASSENGER")
        return register(userWithRole)
    }

    /**
     * Convenience endpoint for driver registration with bus assignment
     * POST /api/auth/register/driver
     * Body: { "userName": "...", "userEmail": "...", "userPhoneNumber": "...", "userPassword": "...", "busPlateNumber": "RAC223M" }
     */
    @PostMapping("/register/driver")
    fun registerDriver(@RequestBody request: RegisterDriverRequest): ResponseEntity<AuthResponse> {
        // Validate required fields
        val validationErrors = validateDriverRequest(request)
        if (validationErrors.isNotEmpty()) {
            throw IllegalArgumentException("Validation failed: ${validationErrors.joinToString(", ")}")
        }

        // Check if bus exists and is available
        val bus = busService.getBusByPlateNumber(request.busPlateNumber.trim())
        
        if (bus.driver != null) {
            throw IllegalArgumentException("Bus with plate number ${request.busPlateNumber} is already assigned to driver: ${bus.driver?.userName}")
        }

        // Create the driver user
        val registerRequest = RegisterRequest(
            userName = request.userName,
            userEmail = request.userEmail,
            userPhoneNumber = request.userPhoneNumber,
            userPassword = request.userPassword
        )
        val user = createUserFromRequest(registerRequest, UserRole.DRIVER)
        val savedDriver = userService.createUser(user)
        
        // Assign driver to bus by plate number
        busService.updateBusWithDriverByPlateNumber(request.busPlateNumber.trim(), savedDriver)

        return ResponseEntity.ok(generateAuthToken(savedDriver))
    }

    /**
     * Convenience endpoint for regular user registration
     * POST /api/auth/register/user
     */
    @PostMapping("/register/user")
    fun registerUser(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        val userWithRole = request.copy(userRole = "USER")
        return register(userWithRole)
    }

    /**
     * Convenience endpoint for admin registration (should be protected in production)
     * POST /api/auth/register/admin
     */
    @PostMapping("/register/admin")
    fun registerAdmin(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        // Validate required fields
        val validationErrors = validateRegisterRequest(request)
        if (validationErrors.isNotEmpty()) {
            throw IllegalArgumentException("Validation failed: ${validationErrors.joinToString(", ")}")
        }

        // Allow ADMIN role registration through this endpoint
        val user = createUserFromRequest(request, UserRole.ADMIN)
        val savedUser = userService.createUser(user)
        
        return ResponseEntity.ok(generateAuthToken(savedUser))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        // Validate input
        if (request.userEmail.isBlank()) {
            throw IllegalArgumentException("userEmail is required")
        }
        if (request.userPassword.isBlank()) {
            throw IllegalArgumentException("password is required")
        }

        val authToken = UsernamePasswordAuthenticationToken(request.userEmail.trim(), request.userPassword)
        val auth = authenticationManager.authenticate(authToken)
        val principal = auth.name

        val user = userService.findByEmailOrPhone(principal)
        val token = jwtService.generateToken(principal, mapOf("role" to user.userRole.name))

        return ResponseEntity.ok(AuthResponse(token))
    }


    @PostMapping("/logout")
    fun logout(authentication: org.springframework.security.core.Authentication?): ResponseEntity<Map<String, String>> {
        // With JWT stateless authentication, logout is handled client-side by removing the token
        // This endpoint is provided for consistency and can be used for logging purposes
        // The authentication parameter is optional to allow logging logout events
        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
    }
}

