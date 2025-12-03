package com.movesmart.demo.repository

import com.movesmart.demo.model.Bus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BusRepository : JpaRepository<Bus, Long> {
    fun findByPlateNumber(plateNumber: String): Bus?
}