package com.movesmart.demo.service

import com.movesmart.demo.dto.BusDTORequest
import com.movesmart.demo.model.Bus
import com.movesmart.demo.repository.BusRepository
import com.movesmart.demo.repository.OrganizationRepository
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BusService(
    private val busRepository: BusRepository,
    private val organizationRepository: OrganizationRepository,
    private val entityManager: EntityManager
) {
    fun createBus(busDTO: BusDTORequest): Bus {

        val organization = organizationRepository.findFirstByOrderByIdAsc()
            ?: throw IllegalStateException("No organization found. Please create an organization first.")
        
        val bus = Bus(
            plateNumber = busDTO.plateNumber,
            capacity = busDTO.capacity,
            route = busDTO.route,
            organization = organization,
            driver = null // No driver assigned initially
        )
        return busRepository.save(bus)
    }

    fun getAllBuses(): List<Bus> {
        // Use findAll() which has JOIN FETCH to ensure driver is loaded
        return busRepository.findAll()
    }

    fun updateBus(id: Long, busDTO: BusDTORequest): Bus {
        val existingBus = busRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Bus not found with ID: $id") }

        val organization = organizationRepository.findFirstByOrderByIdAsc()
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
        // Use findByIdWithDriver to ensure driver is loaded via JOIN FETCH
        return busRepository.findByIdWithDriver(id)
            .orElseThrow { IllegalArgumentException("Bus not found with ID: $id") }
    }

    fun getBusByPlateNumber(plateNumber: String): Bus {
        return busRepository.findByPlateNumber(plateNumber)
            ?: throw IllegalArgumentException("Bus not found with plate number: $plateNumber")
    }

    fun updateBusWithDriver(busId: Long, driver: com.movesmart.demo.model.User): Bus {
        // Ensure driver has a valid userId
        if (driver.userId == null) {
            throw IllegalArgumentException("Driver must have a valid userId")
        }
        
        // Use native query to directly update the driver_id column in the database
        // This ensures the relationship is persisted correctly regardless of entity state
        val updatedRows = busRepository.assignDriverToBus(busId, driver.userId)
        
        if (updatedRows == 0) {
            throw IllegalArgumentException("Bus not found with ID: $busId or update failed")
        }
        
        // The @Modifying annotation with clearAutomatically=true already clears the cache
        // Fetch the bus again using JOIN FETCH to ensure driver is loaded
        return busRepository.findByIdWithDriver(busId)
            .orElseThrow { IllegalArgumentException("Bus not found with ID: $busId") }
    }

    fun updateBusWithDriverByPlateNumber(plateNumber: String, driver: com.movesmart.demo.model.User): Bus {
        // Ensure driver has a valid userId
        if (driver.userId == null) {
            throw IllegalArgumentException("Driver must have a valid userId")
        }
        
        // Use native query to directly update the driver_id column in the database by plate number
        // This ensures the relationship is persisted correctly regardless of entity state
        val updatedRows = busRepository.assignDriverToBusByPlateNumber(plateNumber, driver.userId)
        
        if (updatedRows == 0) {
            throw IllegalArgumentException("Bus not found with plate number: $plateNumber or update failed")
        }
        
        // The @Modifying annotation with clearAutomatically=true already clears the cache
        // Fetch the bus again using JOIN FETCH to ensure driver is loaded
        return busRepository.findByPlateNumber(plateNumber)
            ?: throw IllegalArgumentException("Bus not found with plate number: $plateNumber")
    }
}
