package com.movesmart.demo.dto

data class LoginRequest(
    val userEmail: String,
    val userPassword: String
)

data class AuthResponse(
    val token: String
)

data class RegisterRequest(
    val userName: String,
    val userEmail: String,
    val userPhoneNumber: String,
    val userPassword: String,
    val userRole: String? = null // Optional - will be set by endpoint if not provided
)

data class RegisterDriverRequest(
    val userName: String,
    val userEmail: String,
    val userPhoneNumber: String,
    val userPassword: String,
    val busPlateNumber: String // Required - bus plate number to assign to the driver
)
