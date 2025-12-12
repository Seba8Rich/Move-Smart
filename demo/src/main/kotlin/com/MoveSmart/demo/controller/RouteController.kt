package com.movesmart.demo.controller

import com.movesmart.demo.dto.RouteDTORequest
import com.movesmart.demo.dto.RouteDTOResponse
import com.movesmart.demo.model.Route
import com.movesmart.demo.service.RouteService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/routes")
class RouteController(
    private val routeService: RouteService
) {
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createRoute(@RequestBody request: RouteDTORequest): ResponseEntity<RouteDTOResponse> {
        val createdRoute = routeService.createRoute(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(createdRoute))
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PASSENGER')")
    fun getAllRoutes(): ResponseEntity<List<RouteDTOResponse>> {
        val routes = routeService.getAllRoutes()
        val routeDTOs = routes.map { toDTO(it) }
        return ResponseEntity.ok(routeDTOs)
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PASSENGER')")
    fun getRouteById(@PathVariable id: Long): ResponseEntity<RouteDTOResponse> {
        val route = routeService.getRouteById(id)
        return ResponseEntity.ok(toDTO(route))
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateRoute(
        @PathVariable id: Long,
        @RequestBody request: RouteDTORequest
    ): ResponseEntity<RouteDTOResponse> {
        val updatedRoute = routeService.updateRoute(id, request)
        return ResponseEntity.ok(toDTO(updatedRoute))
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteRoute(@PathVariable id: Long): ResponseEntity<Map<String, String>> {
        if (routeService.deleteRoute(id)) {
            return ResponseEntity.ok(mapOf("message" to "Route deleted successfully"))
        }
        throw IllegalArgumentException("Route not found with ID: $id")
    }
    
    private fun toDTO(route: Route): RouteDTOResponse {
        return RouteDTOResponse(
            id = route.id,
            routeId = route.routeId,
            startStation = route.startStation,
            endStation = route.endStation,
            distanceKm = route.distanceKm
        )
    }
}
