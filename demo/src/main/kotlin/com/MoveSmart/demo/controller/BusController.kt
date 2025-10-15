package com.movesmart.demo.controller

import com.movesmart.demo.dto.BusDTORequest
import com.movesmart.demo.model.Bus
import com.movesmart.demo.service.BusService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/buses")
class BusController(
    private val busService: BusService
) {

    @PostMapping
    fun createBus(@RequestBody busDTO: BusDTORequest): Bus = busService.createBus(busDTO)

    @GetMapping
    fun getAllBuses(): List<Bus> = busService.getAllBuses()

    @GetMapping("/{id}")
    fun getBusById(@PathVariable id: Long): Bus = busService.getBusById(id)
    
    @PutMapping("/{id}")
    fun updateBus(@PathVariable id: Long, @RequestBody busDTO: BusDTORequest): Bus = busService.updateBus(id, busDTO)

    @DeleteMapping("/{id}")
    fun deleteBus(@PathVariable id: Long): ResponseEntity<String> {
        return if (busService.deleteBus(id)) {
            ResponseEntity.ok("Bus deleted successfully")
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
