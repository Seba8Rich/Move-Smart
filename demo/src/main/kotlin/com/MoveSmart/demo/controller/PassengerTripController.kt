package com.movesmart.demo.controller

import com.movesmart.demo.dto.PassengerTripDTORequest
import com.movesmart.demo.dto.PassengerTripDTOResponse
import com.movesmart.demo.service.PassengerTripService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/passengerTrips")
class PassengerTripController (
    private val passengerTripService: PassengerTripService) {
    
    @PostMapping
    fun createTrip(@RequestBody request: PassengerTripDTORequest): ResponseEntity<PassengerTripDTOResponse> {
        val savedTrip = passengerTripService.createPassengerTrip(request)
        return ResponseEntity.ok(PassengerTripDTOResponse.fromEntity(savedTrip))
    }

    @GetMapping
    fun getTrips(): ResponseEntity<List<PassengerTripDTOResponse>> {
        val trips = passengerTripService.getPassengerTrips()
        val tripDTOs = trips.map { PassengerTripDTOResponse.fromEntity(it) }
        return ResponseEntity.ok(tripDTOs)
    }
    
    @GetMapping("/{id}")
    fun getTripById(@PathVariable id: Long): ResponseEntity<PassengerTripDTOResponse> {
        val trip = passengerTripService.getPassengerTripById(id)
        return ResponseEntity.ok(PassengerTripDTOResponse.fromEntity(trip))
    }
}