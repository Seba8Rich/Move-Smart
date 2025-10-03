package com.MoveSmart.demo.service

import com.MoveSmart.demo.repository.RouteRepository
import com.movesmart.demo.model.Route

class RouteService(private  val routeRepository: RouteRepository) {

    fun createRoute(route: Route): Route = routeRepository.save(route)
}