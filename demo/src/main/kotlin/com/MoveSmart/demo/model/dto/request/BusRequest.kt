package com.movesmart.demo.model.dto.request

data class BusRequest(
    val plateNumber: String,
    val capacity: Int,
    val route: String,
    val organizationId: Long
)
