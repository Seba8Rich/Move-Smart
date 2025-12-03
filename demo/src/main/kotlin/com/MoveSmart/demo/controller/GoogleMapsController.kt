package com.movesmart.demo.controller

import com.movesmart.demo.service.GoogleMapsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/google-maps")
class GoogleMapsController(
    private val googleMapsService: GoogleMapsService
) {

    @PostMapping("/geocode")
    fun geocode(@RequestBody request: GoogleMapsService.GeocodeRequest): ResponseEntity<Map<String, Any>> {
        val result = googleMapsService.geocode(request)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/geocode")
    fun geocodeGet(
        @RequestParam(required = false) address: String?,
        @RequestParam(required = false) latlng: String?,
        @RequestParam(required = false) placeId: String?
    ): ResponseEntity<Map<String, Any>> {
        val request = GoogleMapsService.GeocodeRequest(
            address = address,
            latlng = latlng,
            placeId = placeId
        )
        val result = googleMapsService.geocode(request)
        return ResponseEntity.ok(result)
    }
}

