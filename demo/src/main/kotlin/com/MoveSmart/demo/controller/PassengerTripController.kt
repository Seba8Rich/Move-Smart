package com.movesmart.demo.controller

import com.movesmart.demo.dto.PassengerTripDTORequest
import com.movesmart.demo.dto.PassengerTripDTOResponse
import com.movesmart.demo.model.PassengerTrip
import com.movesmart.demo.service.PassengerTripService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/passengerTrips")
class PassengerTripController(
    private val passengerTripService: PassengerTripService
) {
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createTrip(@RequestBody request: PassengerTripDTORequest): ResponseEntity<PassengerTripDTOResponse> {
        val savedTrip = passengerTripService.createPassengerTrip(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(savedTrip))
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getTrips(): ResponseEntity<List<PassengerTripDTOResponse>> {
        val trips = passengerTripService.getPassengerTrips()
        val tripDTOs = trips.map { toDTO(it) }
        return ResponseEntity.ok(tripDTOs)
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getTripById(@PathVariable id: Long): ResponseEntity<PassengerTripDTOResponse> {
        val trip = passengerTripService.getPassengerTripById(id)
        return ResponseEntity.ok(toDTO(trip))
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateTrip(
        @PathVariable id: Long,
        @RequestBody request: PassengerTripDTORequest
    ): ResponseEntity<PassengerTripDTOResponse> {
        val updatedTrip = passengerTripService.updatePassengerTrip(id, request)
        return ResponseEntity.ok(toDTO(updatedTrip))
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteTrip(@PathVariable id: Long): ResponseEntity<Map<String, String>> {
        if (passengerTripService.deletePassengerTrip(id)) {
            return ResponseEntity.ok(mapOf("message" to "Passenger trip deleted successfully"))
        }
        throw IllegalArgumentException("Passenger trip not found with ID: $id")
    }
    
    private fun toDTO(trip: PassengerTrip): PassengerTripDTOResponse {
        return PassengerTripDTOResponse.fromEntity(trip)
    }
}