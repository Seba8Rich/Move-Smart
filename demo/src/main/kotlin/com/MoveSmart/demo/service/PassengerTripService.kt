package com.movesmart.demo.service

import com.movesmart.demo.dto.PassengerTripDTORequest
import com.movesmart.demo.repository.PassengerTripRepository
import com.movesmart.demo.model.PassengerTrip
import com.movesmart.demo.model.TripStatus
import com.movesmart.demo.repository.BusRepository
import com.movesmart.demo.repository.RouteRepository
import com.movesmart.demo.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class PassengerTripService(
    private val passengerTripRepository: PassengerTripRepository,
    private val busRepository: BusRepository,
    private val routeRepository: RouteRepository,
    private val userRepository: UserRepository
) {

    fun createPassengerTrip(request: PassengerTripDTORequest): PassengerTrip {
        val passenger = userRepository.findById(request.userId)
            .orElseThrow { IllegalArgumentException("Passenger not found with ID: ${request.userId}") }

        val route = routeRepository.findById(request.routeId)
            .orElseThrow { IllegalArgumentException("Route not found with ID: ${request.routeId}") }

        val bus = busRepository.findById(request.busId)
            .orElseThrow { IllegalArgumentException("Bus not found with ID: ${request.busId}") }

        val trip = PassengerTrip(
            passenger = passenger,
            route = route,
            bus = bus,
            startStation = request.startStation,
            endStation = request.endStation,
            tripStatus = TripStatus.valueOf(request.tripStatus.uppercase())
        )

        return passengerTripRepository.save(trip)
    }

    fun getPassengerTrips(): List<PassengerTrip> = passengerTripRepository.findAll()
}

