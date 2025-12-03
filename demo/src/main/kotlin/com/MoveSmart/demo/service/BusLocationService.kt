package com.movesmart.demo.service

import com.movesmart.demo.repository.BusLocationRepository
import com.movesmart.demo.model.BusLocation
import org.springframework.stereotype.Service

@Service
class BusLocationService(
    private val busLocationRepository: BusLocationRepository
) {

    fun createBusLocation(busLocation: BusLocation): BusLocation = busLocationRepository.save(busLocation)
}