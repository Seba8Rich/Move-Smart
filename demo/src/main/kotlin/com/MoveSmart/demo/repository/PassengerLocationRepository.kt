package com.movesmart.demo.repository

import com.movesmart.demo.model.PassengerLocation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PassengerLocationRepository: JpaRepository<PassengerLocation, Long> {
    
    @Query("SELECT pl FROM PassengerLocation pl WHERE pl.passenger.userId = :passengerId AND pl.recordedAt = (SELECT MAX(pl2.recordedAt) FROM PassengerLocation pl2 WHERE pl2.passenger.userId = :passengerId)")
    fun findLatestByPassengerId(@Param("passengerId") passengerId: Long): PassengerLocation?
    
    @Query("SELECT pl FROM PassengerLocation pl WHERE pl.passenger.userId = :passengerId ORDER BY pl.recordedAt DESC")
    fun findAllByPassengerId(@Param("passengerId") passengerId: Long): List<PassengerLocation>
}
