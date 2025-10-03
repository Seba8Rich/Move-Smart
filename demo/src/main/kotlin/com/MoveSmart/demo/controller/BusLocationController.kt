package com.movesmart.demo.controller

import com.movesmart.demo.model.BusLocation
import com.movesmart.demo.service.BusLocationService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bus-locations")
class BusLocationController(
    private val busLocationService: BusLocationService
) {

    @PostMapping
    fun createBusLocation(@RequestBody busLocation: BusLocation): BusLocation {
        return busLocationService.createBusLocation(busLocation)
    }
}
