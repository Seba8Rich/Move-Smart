package com.movesmart.demo.dto

import com.movesmart.demo.model.PassengerLocation
import java.time.LocalDateTime

data class PassengerLocationRequest(
    val latitude: Double,
    val longitude: Double
)

data class PassengerLocationResponse(
    val id: Long,
    val passengerId: Long,
    val latitude: Double,
    val longitude: Double,
    val recordedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(location: PassengerLocation): PassengerLocationResponse {
            return PassengerLocationResponse(
                id = location.id,
                passengerId = location.passenger.userId ?: 0,
                latitude = location.latitude,
                longitude = location.longitude,
                recordedAt = location.recordedAt
            )
        }
    }
}
