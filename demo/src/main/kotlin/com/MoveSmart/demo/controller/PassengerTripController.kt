package com.movesmart.demo.controller

import com.movesmart.demo.dto.PassengerTripDTORequest
import com.movesmart.demo.model.PassengerTrip
import com.movesmart.demo.service.PassengerTripService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/passengerTrips")
class PassengerTripController (
    private val passengerTripService: PassengerTripService) {
    @PostMapping
    fun createTrip(@RequestBody request: PassengerTripDTORequest): ResponseEntity<PassengerTrip> {
        val savedTrip = passengerTripService.createPassengerTrip(request)
        return ResponseEntity.ok(savedTrip)

        @GetMapping
        fun getTrips(): ResponseEntity<List<PassengerTrip>> =
            ResponseEntity.ok(passengerTripService.getPassengerTrips())
    }
}