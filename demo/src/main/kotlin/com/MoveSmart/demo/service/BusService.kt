package com.movesmart.demo.service

import com.movesmart.demo.dto.BusDTORequest
import com.movesmart.demo.model.Bus
import com.movesmart.demo.repository.BusRepository
import com.movesmart.demo.repository.OrganizationRepository
import org.springframework.stereotype.Service

@Service
class BusService(
    private val busRepository: BusRepository,
    private val organizationRepository: OrganizationRepository
) {
    fun createBus(busDTO: BusDTORequest): Bus {

        val organization = organizationRepository.findAll().firstOrNull()
            ?: throw IllegalStateException("No organization found. Please create an organization first.")
        
        val bus = Bus(
            plateNumber = busDTO.plateNumber,
            capacity = busDTO.capacity,
            route = busDTO.route,
            organization = organization
        )
        return busRepository.save(bus)
    }

    fun getAllBuses(): List<Bus> = busRepository.findAll()

    fun updateBus(id: Long, busDTO: BusDTORequest): Bus {
        val existingBus = busRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Bus not found with ID: $id") }

        val organization = organizationRepository.findAll().firstOrNull()
            ?: throw IllegalStateException("No organization found. Please create an organization first.")

        val updatedBus = existingBus.copy(
            plateNumber = busDTO.plateNumber,
            capacity = busDTO.capacity,
            route = busDTO.route,
            organization = organization
        )

        return busRepository.save(updatedBus)
    }

    fun deleteBus(id: Long): Boolean {
        return if (busRepository.existsById(id)) {
            busRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    fun getBusById(id: Long): Bus {
        return busRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Bus not found with ID: $id") }
    }
}
