package com.movesmart.demo.dto

import com.movesmart.demo.model.PassengerTrip
import com.movesmart.demo.model.TripStatus
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
    val updatedAt: LocalDateTime,
    val organizationName: String
) {
    companion object {
        fun fromEntity(trip: PassengerTrip): PassengerTripDTOResponse {
            return PassengerTripDTOResponse(
                id = trip.id,
                userId = trip.passenger?.userId ?: 0,
                routeId = trip.route?.id ?: 0,
                busId = trip.bus?.id ?: 0,
                startStation = trip.startStation,
                endStation = trip.endStation,
                tripStatus = trip.tripStatus.name,
                createdAt = trip.createdAt,
                updatedAt = trip.updatedAt,
                organizationName = trip.bus?.organization?.name ?: ""
            )
        }
    }
}
