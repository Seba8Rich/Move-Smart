package com.movesmart.demo.controller

import com.MoveSmart.demo.service.RouteService
import com.movesmart.demo.model.Route
import org.springframework.web.bind.annotation.RequestBody

class RouteController (private  val routeService: RouteService){

    fun createRoute(@RequestBody route: Route): Route{
            return routeService.createRoute(route)

        }
    }
