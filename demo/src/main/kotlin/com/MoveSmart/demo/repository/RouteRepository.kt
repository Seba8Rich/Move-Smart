package com.movesmart.demo.repository

import com.movesmart.demo.model.Bus
import com.movesmart.demo.model.Route
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface RouteRepository : JpaRepository<Route, Long> {
    @Query("SELECT r FROM Route r WHERE r.startStation = :startStation AND r.endStation = :endStation")
    fun findByStartStationAndEndStation(
        @Param("startStation") startStation: String,
        @Param("endStation") endStation: String
    ): List<Route>
    
    @Query("SELECT r FROM Route r JOIN r.buses b WHERE b.plateNumber = :plateNumber AND r.startStation = :startStation AND r.endStation = :endStation")
    fun findByPlateNumberAndStartStationAndEndStation(
        @Param("plateNumber") plateNumber: String,
        @Param("startStation") startStation: String,
        @Param("endStation") endStation: String
    ): List<Route>
    
    @Query("SELECT r FROM Route r WHERE r.routeId = :routeId")
    fun findByRouteId(@Param("routeId") routeId: Long): Route?
}