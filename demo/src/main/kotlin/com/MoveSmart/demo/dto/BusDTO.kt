package com.movesmart.demo.dto

data class BusDTORequest(
    val plateNumber: String,
    val capacity: Int,
    val route: String,
    val organizationId: Long
)
data class  BusDTOResponse(

    val id: Long,
    val plateNumber: String,
    val capacity: Int

)