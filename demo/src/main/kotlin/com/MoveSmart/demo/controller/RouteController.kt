package com.movesmart.demo.controller

import com.movesmart.demo.dto.RouteDTORequest
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
    fun createRoute(@RequestBody request: RouteDTORequest): ResponseEntity<Route> {
        val createdRoute = routeService.createRoute(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoute)
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllRoutes(): ResponseEntity<List<Route>> {
        val routes = routeService.getAllRoutes()
        return ResponseEntity.ok(routes)
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getRouteById(@PathVariable id: Long): ResponseEntity<Route> {
        val route = routeService.getRouteById(id)
        return ResponseEntity.ok(route)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateRoute(@PathVariable id: Long, @RequestBody request: RouteDTORequest): ResponseEntity<Route> {
        val updatedRoute = routeService.updateRoute(id, request)
        return ResponseEntity.ok(updatedRoute)
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
