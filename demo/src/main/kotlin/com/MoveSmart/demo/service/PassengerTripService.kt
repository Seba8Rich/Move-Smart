package com.movesmart.demo.service

import com.movesmart.demo.dto.PassengerTripDTORequest
import com.movesmart.demo.dto.PassengerTripDTOResponse
import com.movesmart.demo.repository.PassengerTripRepository
import com.movesmart.demo.model.PassengerTrip
import com.movesmart.demo.model.TripStatus
import com.movesmart.demo.repository.BusRepository
import com.movesmart.demo.repository.RouteRepository
import com.movesmart.demo.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
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

        val tripStatus = try {
            TripStatus.valueOf(request.tripStatus.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid trip status: ${request.tripStatus}. Valid values are: ${TripStatus.values().joinToString { it.name }}")
        }

        val trip = PassengerTrip(
            passenger = passenger,
            route = route,
            bus = bus,
            startStation = request.startStation,
            endStation = request.endStation,
            tripStatus = tripStatus
        )

        return passengerTripRepository.save(trip)
    }

    fun getPassengerTrips(): List<PassengerTrip> = passengerTripRepository.findAll()
    
    fun getPassengerTripById(id: Long): PassengerTrip {
        return passengerTripRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Passenger trip not found with ID: $id") }
    }

    fun updatePassengerTrip(id: Long, request: PassengerTripDTORequest): PassengerTrip {
        val existingTrip = passengerTripRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Passenger trip not found with ID: $id") }

        val passenger = userRepository.findById(request.userId)
            .orElseThrow { IllegalArgumentException("Passenger not found with ID: ${request.userId}") }

        val route = routeRepository.findById(request.routeId)
            .orElseThrow { IllegalArgumentException("Route not found with ID: ${request.routeId}") }

        val bus = busRepository.findById(request.busId)
            .orElseThrow { IllegalArgumentException("Bus not found with ID: ${request.busId}") }

        val tripStatus = try {
            TripStatus.valueOf(request.tripStatus.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid trip status: ${request.tripStatus}. Valid values are: ${TripStatus.values().joinToString { it.name }}")
        }

        val updatedTrip = existingTrip.copy(
            passenger = passenger,
            route = route,
            bus = bus,
            startStation = request.startStation,
            endStation = request.endStation,
            tripStatus = tripStatus
        )

        return passengerTripRepository.save(updatedTrip)
    }
    
    fun deletePassengerTrip(id: Long): Boolean {
        return if (passengerTripRepository.existsById(id)) {
            passengerTripRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}

