package com.movesmart.demo.dto

data class UserDTORequest(
    val userLocation: String,
    val userId: Long,
    val userEmail: String,
    val userName: String,
    val userPhoneNumber: String,
    val userPassword:String,
    val userRole: String
)
data class UserDTOResponse(
    val id: Long,
    val name: String,
    val email: String?
)
