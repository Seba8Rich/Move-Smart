package com.movesmart.demo.controller

import com.movesmart.demo.dto.RouteDTORequest
import com.movesmart.demo.dto.RouteDTOResponse
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
    fun createRoute(@RequestBody request: RouteDTORequest): ResponseEntity<Any> {
        return try {
            val createdRoute = routeService.createRoute(request)
            val response = RouteDTOResponse(
                id = createdRoute.id, // Auto-increment primary key
                routeId = createdRoute.routeId, // User-defined display ID (optional)
                startStation = createdRoute.startStation,
                endStation = createdRoute.endStation,
                distanceKm = createdRoute.distanceKm
            )
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Bad Request", "message" to (ex.message ?: "Failed to create route")))
        } catch (ex: IllegalStateException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Bad Request", "message" to (ex.message ?: "Failed to create route")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Internal Server Error", "message" to (ex.message ?: "An error occurred")))
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllRoutes(): ResponseEntity<List<RouteDTOResponse>> {
        val routes = routeService.getAllRoutes()
        val routeDTOs = routes.map { route ->
            RouteDTOResponse(
                id = route.id, // Auto-increment primary key
                routeId = route.routeId, // User-defined display ID (optional)
                startStation = route.startStation,
                endStation = route.endStation,
                distanceKm = route.distanceKm
            )
        }
        return ResponseEntity.ok(routeDTOs)
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getRouteById(@PathVariable id: Long): ResponseEntity<RouteDTOResponse> {
        val route = routeService.getRouteById(id)
        val response = RouteDTOResponse(
            id = route.id, // Auto-increment primary key
            routeId = route.routeId, // User-defined display ID (optional)
            startStation = route.startStation,
            endStation = route.endStation,
            distanceKm = route.distanceKm
        )
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateRoute(@PathVariable id: Long, @RequestBody request: RouteDTORequest): ResponseEntity<Any> {
        return try {
            val updatedRoute = routeService.updateRoute(id, request)
            val response = RouteDTOResponse(
                id = updatedRoute.id, // Auto-increment primary key
                routeId = updatedRoute.routeId, // User-defined display ID (optional)
                startStation = updatedRoute.startStation,
                endStation = updatedRoute.endStation,
                distanceKm = updatedRoute.distanceKm
            )
            ResponseEntity.ok(response)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Bad Request", "message" to (ex.message ?: "Failed to update route")))
        } catch (ex: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Internal Server Error", "message" to (ex.message ?: "An error occurred")))
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteRoute(@PathVariable id: Long): ResponseEntity<String> {
        return if (routeService.deleteRoute(id)) {
            ResponseEntity.ok("Route deleted successfully")
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
