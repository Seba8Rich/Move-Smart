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
    
    @GetMapping("/my-bus")
    @PreAuthorize("hasRole('DRIVER')")
    fun getMyBus(authentication: Authentication): ResponseEntity<Any> {
        return try {
            val username = authentication.name
            val driver = userService.findByEmailOrPhone(username)
            
            val buses = busService.getBusesByDriverId(driver.userId ?: throw IllegalArgumentException("Driver ID not found"))
            
            if (buses.isEmpty()) {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("message" to "No bus assigned to this driver"))
            } else {
                // Return the first bus (driver should only have one bus assigned)
                ResponseEntity.ok(buses.first())
            }
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Bad Request", "message" to (ex.message ?: "Failed to get bus")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Internal Server Error", "message" to (ex.message ?: "An error occurred")))
        }
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
