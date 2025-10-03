package com.movesmart.demo.model.dto.response

data class BusResponse(
    val id: Long,
    val plateNumber: String,
    val capacity: Int,
    val route: String,
    val organizationName: String
)
