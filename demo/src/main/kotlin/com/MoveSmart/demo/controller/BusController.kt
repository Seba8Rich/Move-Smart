package com.movesmart.demo.controller

import com.movesmart.demo.model.Bus
import com.movesmart.demo.service.BusService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/bus")
class BusController(
    private val busService: BusService
) {

    @PostMapping
    fun createBus(@RequestBody bus: Bus): Bus = busService.createBus(bus)

    @GetMapping
    fun getAllBuses(): List<Bus> = busService.getAllBuses()
}
