package com.movesmart.demo.dto

import com.movesmart.demo.model.PassengerTrip
import java.time.LocalDateTime

data class PassengerTripDTORequest(
    val userId: Long,
    val routeId: Long,
    val busId: Long,
    val startStation: String,
    val endStation: String,
    val tripStatus: String = "BOOKED"
)

data class PassengerTripDTOResponse(
    val id: Long,
    val userId: Long,
    val routeId: Long,
    val busId: Long,
    val startStation: String,
    val endStation: String,
    val tripStatus: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(trip: PassengerTrip): PassengerTripDTOResponse {
            return PassengerTripDTOResponse(
                id = trip.id,
                userId = trip.getPassengerId(),
                routeId = trip.getRouteId(),
                busId = trip.getBusId(),
                startStation = trip.startStation,
                endStation = trip.endStation,
                tripStatus = trip.tripStatus.name,
                createdAt = trip.createdAt,
                updatedAt = trip.updatedAt
            )
        }
    }
}
