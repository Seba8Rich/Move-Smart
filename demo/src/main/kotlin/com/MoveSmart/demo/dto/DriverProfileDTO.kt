package com.movesmart.demo.dto

data class DriverProfileResponse(
    val driver: DriverInfo,
    val assignedBus: AssignedBusInfo?
)

data class DriverInfo(
    val userId: Long,
    val userName: String,
    val userEmail: String,
    val userPhoneNumber: String,
    val userRole: String
)

data class AssignedBusInfo(
    val busId: Long,
    val plateNumber: String,
    val capacity: Int,
    val route: RouteInfo?
)

data class RouteInfo(
    val id: Long,
    val routeId: Long?,
    val routeName: String, // "StartStation - EndStation"
    val startStation: String,
    val endStation: String,
    val distanceKm: Double
)
