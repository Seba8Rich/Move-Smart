package com.movesmart.demo.controller

import com.movesmart.demo.dto.BusDTORequest
import com.movesmart.demo.model.Bus
import com.movesmart.demo.service.BusService
import com.movesmart.demo.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/buses")
class BusController(
    private val busService: BusService,
    private val userService: UserService
) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createBus(@RequestBody busDTO: BusDTORequest): ResponseEntity<Bus> {
        val createdBus = busService.createBus(busDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBus)
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    fun getAllBuses(): ResponseEntity<List<Bus>> {
        val buses = busService.getAllBuses()
        return ResponseEntity.ok(buses)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    fun getBusById(@PathVariable id: String): ResponseEntity<Bus> {
        val bus = getBusByIdOrPlateNumber(id)
        return ResponseEntity.ok(bus)
    }
    
    private fun getBusByIdOrPlateNumber(identifier: String): Bus {
        return try {
            val busId = identifier.toLong()
            busService.getBusById(busId)
        } catch (e: NumberFormatException) {
            busService.getBusByPlateNumber(identifier)
        }
    }
    
    @GetMapping("/my-bus")
    @PreAuthorize("hasRole('DRIVER')")
    fun getMyBus(authentication: Authentication): ResponseEntity<Bus> {
        val username = authentication.name
        val driver = userService.findByEmailOrPhone(username)
        
        val driverId = driver.userId ?: throw IllegalArgumentException("Driver ID not found")
        val buses = busService.getBusesByDriverId(driverId)
        
        if (buses.isEmpty()) {
            throw IllegalArgumentException("No bus assigned to this driver")
        }
        
        // Return the first bus (driver should only have one bus assigned)
        return ResponseEntity.ok(buses.first())
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateBus(@PathVariable id: Long, @RequestBody busDTO: BusDTORequest): ResponseEntity<Bus> {
        val updatedBus = busService.updateBus(id, busDTO)
        return ResponseEntity.ok(updatedBus)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteBus(@PathVariable id: Long): ResponseEntity<Map<String, String>> {
        if (busService.deleteBus(id)) {
            return ResponseEntity.ok(mapOf("message" to "Bus deleted successfully"))
        }
        throw IllegalArgumentException("Bus not found with ID: $id")
    }
}
