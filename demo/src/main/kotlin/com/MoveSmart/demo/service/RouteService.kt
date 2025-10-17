package com.movesmart.demo.service

import com.movesmart.demo.dto.RouteDTORequest
import com.movesmart.demo.model.Route
import com.movesmart.demo.repository.BusRepository
import com.movesmart.demo.repository.RouteRepository
import org.springframework.stereotype.Service


@Service
class RouteService(
    private val routeRepository: RouteRepository,
    private val busRepository: BusRepository
) {
    fun createRoute(request: RouteDTORequest): Route {
        val bus = busRepository.findById(request.busId)
            .orElseThrow { IllegalArgumentException("Bus not found with ID ${request.busId}") }

        val route = Route(
            startStation = request.startStation,
            endStation = request.endStation,
            distanceKm = request.distanceKm,
            bus = bus
        )

        return routeRepository.save(route)
    }

    fun getAllRoutes(): List<Route> = routeRepository.findAll()
    
    fun getRouteById(id: Long): Route {
        return routeRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Route not found with ID: $id") }
    }

    fun updateRoute(id: Long, request: RouteDTORequest): Route {
        val existingRoute = routeRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Route not found with ID: $id") }
        
        val bus = busRepository.findById(request.busId)
            .orElseThrow { IllegalArgumentException("Bus not found with ID ${request.busId}") }
        
        val updatedRoute = existingRoute.copy(
            startStation = request.startStation,
            endStation = request.endStation,
            distanceKm = request.distanceKm,
            bus = bus
        )
        
        return routeRepository.save(updatedRoute)
    }

    fun deleteRoute(id: Long): Boolean {
        return if (routeRepository.existsById(id)) {
            routeRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}
