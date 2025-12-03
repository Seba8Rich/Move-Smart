package com.movesmart.demo.controller

import com.movesmart.demo.dto.BusDTORequest
import com.movesmart.demo.model.Bus
import com.movesmart.demo.service.BusService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/buses")
class BusController(
    private val busService: BusService
) {

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createBus(@RequestBody busDTO: BusDTORequest): ResponseEntity<Bus> {
        val createdBus = busService.createBus(busDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBus)
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllBuses(): ResponseEntity<List<Bus>> {
        val buses = busService.getAllBuses()
        return ResponseEntity.ok(buses)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getBusById(@PathVariable id: String): ResponseEntity<Bus> {
        val bus = try {
            // Try to parse as Long (ID)
            val busId = id.toLong()
            busService.getBusById(busId)
        } catch (e: NumberFormatException) {
            // If not a number, treat as plate number
            busService.getBusByPlateNumber(id)
        }
        return ResponseEntity.ok(bus)
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateBus(@PathVariable id: Long, @RequestBody busDTO: BusDTORequest): ResponseEntity<Bus> {
        val updatedBus = busService.updateBus(id, busDTO)
        return ResponseEntity.ok(updatedBus)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteBus(@PathVariable id: Long): ResponseEntity<String> {
        return if (busService.deleteBus(id)) {
            ResponseEntity.ok("Bus deleted successfully")
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
