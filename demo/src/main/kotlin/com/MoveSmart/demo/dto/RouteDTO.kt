package com.movesmart.demo.dto

data class RouteDTORequest(
    val startStation: String,
    val endStation: String,
    val distanceKm: Double,
    val busId: Long
)
data class RouteDTORespond(
    val id: Long,
    val startStation: String,
    val endStation: String,
    val distanceKm: Double
)

