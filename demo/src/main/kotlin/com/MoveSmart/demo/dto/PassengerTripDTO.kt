package com.movesmart.demo.dto

import java.time.LocalDateTime

data class PassengerTripDTORequest(
    val userId: Long,
    val routeId: Long,
    val busId: Long,
    val startStation: String,
    val endStation: String,
    val tripStatus: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
data class PassengerTripDTOResponse(
    val userId: Long,
    val routeId: Long,
    val busId: Long,
    val startStation: String,
    val endStation: String,
    val tripStatus: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
