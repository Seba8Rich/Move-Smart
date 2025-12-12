package com.movesmart.demo.dto

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
