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
    ): ResponseEntity<Any> {
        return try {
            val username = authentication.name
            val user = userService.findByEmailOrPhone(username)
            val userId = user.userId ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Bad Request", "message" to "User ID not found"))

            val savedLocation = passengerLocationService.saveOrUpdatePassengerLocation(userId, request)
            ResponseEntity.status(HttpStatus.CREATED).body(savedLocation)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Bad Request", "message" to (ex.message ?: "Failed to save location")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Internal Server Error", "message" to "An error occurred while saving location"))
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PASSENGER')")
    fun getMyLocation(authentication: Authentication): ResponseEntity<Any> {
        return try {
            val username = authentication.name
            val user = userService.findByEmailOrPhone(username)
            val userId = user.userId ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Bad Request", "message" to "User ID not found"))

            val location = passengerLocationService.getLatestPassengerLocation(userId)
            
            if (location == null) {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("error" to "Not Found", "message" to "No location found. Please save your location first."))
            } else {
                ResponseEntity.ok(location)
            }
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Bad Request", "message" to (ex.message ?: "Failed to get location")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Internal Server Error", "message" to "An error occurred while getting location"))
        }
    }

    @GetMapping("/me/history")
    @PreAuthorize("hasRole('PASSENGER')")
    fun getMyLocationHistory(authentication: Authentication): ResponseEntity<Any> {
        return try {
            val username = authentication.name
            val user = userService.findByEmailOrPhone(username)
            val userId = user.userId ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Bad Request", "message" to "User ID not found"))

            val locations = passengerLocationService.getAllPassengerLocations(userId)
            ResponseEntity.ok(locations)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Bad Request", "message" to (ex.message ?: "Failed to get location history")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Internal Server Error", "message" to "An error occurred while getting location history"))
        }
    }
}
