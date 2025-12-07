package com.movesmart.demo.service

import com.movesmart.demo.dto.BusDTORequest
import com.movesmart.demo.model.Bus
import com.movesmart.demo.repository.BusRepository
import com.movesmart.demo.repository.OrganizationRepository
import com.movesmart.demo.repository.RouteRepository
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BusService(
    private val busRepository: BusRepository,
    private val organizationRepository: OrganizationRepository,
    private val routeRepository: RouteRepository,
    private val entityManager: EntityManager
) {
    fun createBus(busDTO: BusDTORequest): Bus {

        val organization = organizationRepository.findFirstByOrderByIdAsc()
            ?: throw IllegalStateException("No organization found. Please create an organization first.")
        
        // If routeId is provided, assign bus to existing route
        if (busDTO.routeId != null) {
            val route = routeRepository.findById(busDTO.routeId)
                .orElseThrow { IllegalArgumentException("Route not found with ID: ${busDTO.routeId}") }
            
            // Create bus with route string
            val bus = Bus(
                plateNumber = busDTO.plateNumber,
                capacity = busDTO.capacity,
                route = "${route.startStation} to ${route.endStation}",
                organization = organization,
                driver = null // No driver assigned initially
            )
            
            // Save bus first
            val savedBus = busRepository.save(bus)
            
            // Add bus to route's buses collection (assign bus to route)
            route.buses.add(savedBus)
            routeRepository.save(route)
            
            return savedBus
        } else {
            // Create bus without route assignment
            val bus = Bus(
                plateNumber = busDTO.plateNumber,
                capacity = busDTO.capacity,
                route = "",
                organization = organization,
                driver = null // No driver assigned initially
            )
            return busRepository.save(bus)
        }
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

        // If routeId is provided, assign bus to existing route
        if (busDTO.routeId != null) {
            val route = routeRepository.findById(busDTO.routeId)
                .orElseThrow { IllegalArgumentException("Route not found with ID: ${busDTO.routeId}") }
            
            // Update bus with route string
            val updatedBus = existingBus.copy(
                plateNumber = busDTO.plateNumber,
                capacity = busDTO.capacity,
                route = "${route.startStation} to ${route.endStation}",
                organization = organization
            )
            
            val savedBus = busRepository.save(updatedBus)
            
            // Add bus to route's buses collection if not already assigned
            if (!route.buses.contains(savedBus)) {
                route.buses.add(savedBus)
                routeRepository.save(route)
            }
            
            return savedBus
        } else {
            // Update bus without changing route assignment
            val updatedBus = existingBus.copy(
                plateNumber = busDTO.plateNumber,
                capacity = busDTO.capacity,
                route = existingBus.route, // Keep existing route
                organization = organization
            )
            return busRepository.save(updatedBus)
        }
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

    fun unassignDriverFromBus(driverId: Long): Int {
        // Validate driverId
        if (driverId <= 0) {
            throw IllegalArgumentException("Invalid driver ID: $driverId")
        }
        
        // Use native query to unassign driver from all buses
        val updatedRows = busRepository.unassignDriverFromAllBuses(driverId)
        
        // Note: updatedRows can be 0 if driver wasn't assigned to any bus
        // This is not an error, just means there was nothing to unassign
        return updatedRows
    }

    fun isDriverAssignedToBus(driverId: Long): Boolean {
        // Check if driver is assigned to any bus
        val buses = busRepository.findByDriverId(driverId)
        return buses.isNotEmpty()
    }

    fun getBusesByDriverId(driverId: Long): List<Bus> {
        // Get all buses assigned to a specific driver
        return busRepository.findByDriverId(driverId)
    }

    fun getBusWithRouteInfo(busId: Long): Pair<Bus, com.movesmart.demo.model.Route?> {
        val bus = getBusById(busId)
        // Find the route(s) this bus is assigned to
        val routes = routeRepository.findByBusId(busId)
        // Return the first route (bus typically assigned to one route)
        val route = routes.firstOrNull()
        return Pair(bus, route)
    }
}
