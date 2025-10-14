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
}
