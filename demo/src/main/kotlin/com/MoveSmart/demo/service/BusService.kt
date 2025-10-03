package com.movesmart.demo.service
import com.movesmart.demo.model.Bus
import com.movesmart.demo.model.dto.request.BusRequest
import com.movesmart.demo.model.dto.response.BusResponse
import com.movesmart.demo.repository.BusRepository
import com.movesmart.demo.repository.OrganizationRepository
import org.springframework.stereotype.Service


@Service
class BusService(
    private val busRepository: BusRepository,
    private val organizationRepository: OrganizationRepository
) {

    fun createBus(request: BusRequest): BusResponse {
        val organization = organizationRepository.findById(request.organizationId)
            .orElseThrow { IllegalArgumentException("Organization not found") }

        val bus = busRepository.save(
            Bus(
                plateNumber = request.plateNumber,
                capacity = request.capacity,
                route = request.route,
                organization = organization
            )
        )

        return bus.toResponse()
    }

    fun getAllBuses(): List<BusResponse> =
        busRepository.findAll().map { it.toResponse() }

    fun getBusById(id: Long): BusResponse =
        busRepository.findById(id).orElseThrow { IllegalArgumentException("Bus not found") }
            .toResponse()

    fun updateBus(id: Long, request: BusRequest): BusResponse {
        val existingBus = busRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Bus not found") }

        val organization = organizationRepository.findById(request.organizationId)
            .orElseThrow { IllegalArgumentException("Organization not found") }

        val updatedBus = existingBus.copy(
            plateNumber = request.plateNumber,
            capacity = request.capacity,
            route = request.route,
            organization = organization
        )

        return busRepository.save(updatedBus).toResponse()
    }

    fun deleteBus(id: Long) {
        if (!busRepository.existsById(id)) {
            throw IllegalArgumentException("Bus not found")
        }
        busRepository.deleteById(id)
    }

    private fun Bus.toResponse() = BusResponse(
        id = this.id,
        plateNumber = this.plateNumber,
        capacity = this.capacity,
        route = this.route,
        organizationName = this.organization.name
    )
}

