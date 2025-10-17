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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    fun createBusLocation(@RequestBody busLocation: BusLocation): ResponseEntity<BusLocation> {
        val createdLocation = busLocationService.createBusLocation(busLocation)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLocation)
    }
}
