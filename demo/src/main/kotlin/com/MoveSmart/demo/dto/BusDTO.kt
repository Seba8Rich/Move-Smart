package com.movesmart.demo.dto

data class BusDTORequest(
    val plateNumber: String,
    val capacity: Int,
    val route: String
)
data class  BusDTOResponse(
    val id: Long,
    val plateNumber: String,
    val capacity: Int,
    val route: String,
    val organizationName: String
)

{
    companion object {
        fun fromEntity(bus: com.movesmart.demo.model.Bus): BusDTOResponse {
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
