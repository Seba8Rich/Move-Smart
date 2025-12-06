package com.movesmart.demo.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RouteDTORequest(
    @JsonProperty("routeId")
    val routeId: Long? = null, // Optional user-defined display ID
    @JsonProperty("startStation")
    val startStation: String,
    @JsonProperty("endStation")
    val endStation: String,
    @JsonProperty("distanceKm")
    val distanceKm: Double
)

data class RouteDTOResponse(
    @JsonProperty("id")
    val id: Long, // Auto-increment primary key
    @JsonProperty("routeId")
    val routeId: Long?, // User-defined display ID (optional)
    @JsonProperty("startStation")
    val startStation: String,
    @JsonProperty("endStation")
    val endStation: String,
    @JsonProperty("distanceKm")
    val distanceKm: Double
)
