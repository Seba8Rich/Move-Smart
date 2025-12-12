package com.movesmart.demo.controller

import com.movesmart.demo.dto.PassengerLocationRequest
import com.movesmart.demo.dto.PassengerLocationResponse
import com.movesmart.demo.service.PassengerLocationService
import com.movesmart.demo.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/passenger-locations")
class PassengerLocationController(
    private val passengerLocationService: PassengerLocationService,
    private val userService: UserService
) {

    @PostMapping("/me")
    @PreAuthorize("hasRole('PASSENGER')")
    fun saveMyLocation(
        @RequestBody request: PassengerLocationRequest,
        authentication: Authentication
    ): ResponseEntity<PassengerLocationResponse> {
        val userId = getCurrentUserId(authentication)
        val savedLocation = passengerLocationService.saveOrUpdatePassengerLocation(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLocation)
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PASSENGER')")
    fun getMyLocation(authentication: Authentication): ResponseEntity<PassengerLocationResponse> {
        val userId = getCurrentUserId(authentication)
        val location = passengerLocationService.getLatestPassengerLocation(userId)
        
        if (location == null) {
            throw IllegalArgumentException("No location found. Please save your location first.")
        }
        
        return ResponseEntity.ok(location)
    }

    @GetMapping("/me/history")
    @PreAuthorize("hasRole('PASSENGER')")
    fun getMyLocationHistory(authentication: Authentication): ResponseEntity<List<PassengerLocationResponse>> {
        val userId = getCurrentUserId(authentication)
        val locations = passengerLocationService.getAllPassengerLocations(userId)
        return ResponseEntity.ok(locations)
    }
    
    private fun getCurrentUserId(authentication: Authentication): Long {
        val username = authentication.name
        val user = userService.findByEmailOrPhone(username)
        return user.userId ?: throw IllegalArgumentException("User ID not found")
    }
}
