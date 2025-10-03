package com.movesmart.demo.controller
import com.movesmart.demo.model.dto.request.BusRequest
import com.movesmart.demo.model.dto.response.BusResponse
import com.movesmart.demo.service.BusService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/bus")
class BusController(
    private val busService: BusService
) {
    @PostMapping
    fun createBus(@RequestBody request: BusRequest): BusResponse =
        busService.createBus(request)

    @GetMapping
    fun getAllBuses(): List<BusResponse> =
        busService.getAllBuses()

    @GetMapping("/{id}")
    fun getBusById(@PathVariable id: Long): BusResponse =
        busService.getBusById(id)

    @PutMapping("/{id}")
    fun updateBus(@PathVariable id: Long, @RequestBody request: BusRequest): BusResponse =
        busService.updateBus(id, request)

    @DeleteMapping("/{id}")
    fun deleteBus(@PathVariable id: Long) =
        busService.deleteBus(id)
}