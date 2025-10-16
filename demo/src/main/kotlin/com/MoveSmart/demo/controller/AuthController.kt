package com.movesmart.demo.controller

import com.movesmart.demo.model.User
import com.movesmart.demo.security.JwtService
import com.movesmart.demo.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*

data class LoginRequest(val username: String, val password: String)
data class AuthResponse(val token: String)

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val userService: UserService
) {

    @PostMapping("/register")
    fun register(@RequestBody user: User): ResponseEntity<AuthResponse> {
        val saved = userService.createUser(user)
        val principal = saved.userEmail.ifBlank { saved.userPhoneNumber }
        val token = jwtService.generateToken(principal, mapOf("role" to saved.userRole.name))
        return ResponseEntity.ok(AuthResponse(token))
    }

    @PostMapping("/login")
    fun login(@RequestBody req: LoginRequest): ResponseEntity<Any> {
        return try {
            val authToken = UsernamePasswordAuthenticationToken(req.username, req.password)
            val auth = authenticationManager.authenticate(authToken)
            val principal = auth.name
            val token = jwtService.generateToken(principal)
            ResponseEntity.ok(AuthResponse(token))
        } catch (ex: AuthenticationException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mapOf("message" to "Invalid credentials"))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to (ex.message ?: "Bad request")))
        }
    }
}


