package com.movesmart.demo.controller

import com.movesmart.demo.dto.BusDTORequest
import com.movesmart.demo.model.Bus
import com.movesmart.demo.service.BusService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/busses")
class BusController(
    private val busService: BusService
) {

    @PostMapping
    fun createBus(@RequestBody busDTO: BusDTORequest): Bus = busService.createBus(busDTO)

    @GetMapping
    fun getAllBuses(): List<Bus> = busService.getAllBuses()
}
