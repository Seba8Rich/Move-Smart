package com.movesmart.demo.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
class GoogleMapsService(
    @Value("\${google.maps.api.key:}") private val apiKey: String
) {
    private val restTemplate = RestTemplate()
    private val baseUrl = "https://maps.googleapis.com/maps/api/geocode/json"

    data class GeocodeRequest(
        val address: String? = null,
        val latlng: String? = null,
        val placeId: String? = null
    )

    fun geocode(request: GeocodeRequest): Map<String, Any> {
        if (apiKey.isBlank()) {
            throw IllegalStateException("Google Maps API key is not configured. Please set 'google.maps.api.key' in application.properties")
        }

        val params = mutableMapOf<String, String>()
        params["key"] = apiKey

        when {
            !request.address.isNullOrBlank() -> params["address"] = request.address
            !request.latlng.isNullOrBlank() -> params["latlng"] = request.latlng
            !request.placeId.isNullOrBlank() -> params["place_id"] = request.placeId
            else -> throw IllegalArgumentException("Either 'address', 'latlng', or 'placeId' must be provided")
        }

        val queryString = params.entries.joinToString("&") { 
            "${it.key}=${URLEncoder.encode(it.value, StandardCharsets.UTF_8.toString())}" 
        }
        val url = "$baseUrl?$queryString"

        return try {
            val response = restTemplate.getForObject(url, Map::class.java)
            response as? Map<String, Any> ?: throw IllegalStateException("Invalid response from Google Maps API")
        } catch (e: HttpClientErrorException) {
            throw IllegalArgumentException("Google Maps API error: ${e.responseBodyAsString}", e)
        } catch (e: HttpServerErrorException) {
            throw IllegalStateException("Google Maps API server error: ${e.responseBodyAsString}", e)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to call Google Maps API: ${e.message}", e)
        }
    }
}

