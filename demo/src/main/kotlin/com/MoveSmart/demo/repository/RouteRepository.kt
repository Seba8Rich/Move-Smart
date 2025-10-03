package com.MoveSmart.demo.repository

import com.movesmart.demo.model.Route
import org.springframework.data.jpa.repository.JpaRepository

interface RouteRepository: JpaRepository<Route, Long> {
}