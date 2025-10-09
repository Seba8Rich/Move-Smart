package com.movesmart.demo.controller


import com.movesmart.demo.dto.RouteDTORequest
import com.movesmart.demo.model.Route
import com.movesmart.demo.service.RouteService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/routes")
class RouteController(
    private val routeService: RouteService
) {
    @PostMapping
    fun createRoute(@RequestBody request: RouteDTORequest): Route =
        routeService.createRoute(request)

    @GetMapping
    fun getAllRoutes(): List<Route> =
        routeService.getAllRoutes()
}
