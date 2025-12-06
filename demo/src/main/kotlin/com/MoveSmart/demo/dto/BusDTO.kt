package com.movesmart.demo.dto

import com.movesmart.demo.model.Bus
import kotlin.Long

data class BusDTORequest(
    val plateNumber: String,
    val capacity: Int,
    val routeId: Long? = null
)
data class BusDTOResponse(
    val id: Long,
    val plateNumber: String,
    val capacity: Int,
    val route: String,
    val organizationName: String
) {
    companion object {
        fun fromEntity(bus: Bus): BusDTOResponse {
            return BusDTOResponse(
                id = bus.id,
                plateNumber = bus.plateNumber,
                capacity = bus.capacity,
                route = bus.route,
                organizationName = bus.organization.name
            )

        }
    }
}
