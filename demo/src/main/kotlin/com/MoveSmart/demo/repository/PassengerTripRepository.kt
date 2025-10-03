package com.movesmart.demo.repository

import com.movesmart.demo.model.PassengerTrip
import org.springframework.data.jpa.repository.JpaRepository

interface PassengerTripRepository: JpaRepository<PassengerTrip, Long> {
}