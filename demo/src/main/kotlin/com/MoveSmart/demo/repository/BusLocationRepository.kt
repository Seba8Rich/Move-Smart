package com.movesmart.demo.repository
import com.movesmart.demo.model.BusLocation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BusLocationRepository: JpaRepository<BusLocation, Long> {
}