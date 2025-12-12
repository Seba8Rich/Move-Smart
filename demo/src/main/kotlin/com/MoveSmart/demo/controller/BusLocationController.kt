package com.movesmart.demo.controller

import com.movesmart.demo.model.BusLocation
import com.movesmart.demo.service.BusLocationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bus-locations")
class BusLocationController(
    private val busLocationService: BusLocationService
) {

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER')")
    fun createBusLocation(@RequestBody busLocation: BusLocation): ResponseEntity<BusLocation> {
        // Validate that bus is provided
        if (busLocation.bus.id == 0L) {
            throw IllegalArgumentException("Bus must be provided")
        }
        
        // Validate coordinates
        if (busLocation.latitude < -90 || busLocation.latitude > 90) {
            throw IllegalArgumentException("Latitude must be between -90 and 90")
        }
        if (busLocation.longitude < -180 || busLocation.longitude > 180) {
            throw IllegalArgumentException("Longitude must be between -180 and 180")
        }
        
        val createdLocation = busLocationService.createBusLocation(busLocation)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLocation)
    }
}
