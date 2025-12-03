package com.movesmart.demo.repository

import com.movesmart.demo.model.PassengerTrip
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PassengerTripRepository : JpaRepository<PassengerTrip, Long> {
}