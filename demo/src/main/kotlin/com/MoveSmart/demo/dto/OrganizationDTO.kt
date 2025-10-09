package com.movesmart.demo.dto

data class OrganizationDTORequest(
    val name: String,
    val address: String,
    val contactNumber: String,
    val email: String
)
data class OrganizationDTOResponse(
    val name: String,
    val address: String,
    val contactNumber: String,
    val email: String
)
