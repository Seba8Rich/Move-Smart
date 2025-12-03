package com.movesmart.demo.dto

data class RouteDTORequest(
    val startStation: String,
    val endStation: String,
    val distanceKm: Double,
    val busId: Long
)
data class RouteDTOResponse(
    val id: Long,
    val startStation: String,
    val endStation: String,
    val distanceKm: Double
)

