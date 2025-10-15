package com.movesmart.demo.controller


import com.movesmart.demo.dto.RouteDTORequest
import com.movesmart.demo.model.Route
import com.movesmart.demo.service.RouteService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/routes")
class RouteController(
    private val routeService: RouteService
) {
    @PostMapping
    fun createRoute(@RequestBody request: RouteDTORequest): Route =
        routeService.createRoute(request)

    @GetMapping
    fun getAllRoutes(): List<Route> = routeService.getAllRoutes()
    
    @GetMapping("/{id}")
    fun getRouteById(@PathVariable id: Long): Route = routeService.getRouteById(id)


    @DeleteMapping("/{id}")
    fun deleteRoute(@PathVariable id: Long): ResponseEntity<String> {
        return if (routeService.deleteRoute(id)) {
            ResponseEntity.ok("Route deleted successfully")
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
