package com.movesmart.demo.service

import com.movesmart.demo.dto.PassengerLocationRequest
import com.movesmart.demo.dto.PassengerLocationResponse
import com.movesmart.demo.model.PassengerLocation
import com.movesmart.demo.model.UserRole
import com.movesmart.demo.repository.PassengerLocationRepository
import com.movesmart.demo.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PassengerLocationService(
    private val passengerLocationRepository: PassengerLocationRepository,
    private val userRepository: UserRepository
) {

    fun saveOrUpdatePassengerLocation(passengerId: Long, request: PassengerLocationRequest): PassengerLocationResponse {
        val passenger = userRepository.findById(passengerId)
            .orElseThrow { IllegalArgumentException("Passenger not found with ID: $passengerId") }
        
        if (passenger.userRole != UserRole.PASSENGER) {
            throw IllegalArgumentException("User is not a passenger")
        }

        // Validate coordinates
        if (request.latitude < -90 || request.latitude > 90) {
            throw IllegalArgumentException("Latitude must be between -90 and 90")
        }
        if (request.longitude < -180 || request.longitude > 180) {
            throw IllegalArgumentException("Longitude must be between -180 and 180")
        }

        val location = PassengerLocation(
            passenger = passenger,
            latitude = request.latitude,
            longitude = request.longitude
        )

        val savedLocation = passengerLocationRepository.save(location)
        return PassengerLocationResponse.fromEntity(savedLocation)
    }

    fun getLatestPassengerLocation(passengerId: Long): PassengerLocationResponse? {
        val passenger = userRepository.findById(passengerId)
            .orElseThrow { IllegalArgumentException("Passenger not found with ID: $passengerId") }
        
        if (passenger.userRole != UserRole.PASSENGER) {
            throw IllegalArgumentException("User is not a passenger")
        }

        val location = passengerLocationRepository.findLatestByPassengerId(passengerId)
        return location?.let { PassengerLocationResponse.fromEntity(it) }
    }

    fun getAllPassengerLocations(passengerId: Long): List<PassengerLocationResponse> {
        val passenger = userRepository.findById(passengerId)
            .orElseThrow { IllegalArgumentException("Passenger not found with ID: $passengerId") }
        
        if (passenger.userRole != UserRole.PASSENGER) {
            throw IllegalArgumentException("User is not a passenger")
        }

        val locations = passengerLocationRepository.findAllByPassengerId(passengerId)
        return locations.map { PassengerLocationResponse.fromEntity(it) }
    }
}
