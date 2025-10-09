package com.movesmart.demo.service

import com.movesmart.demo.model.Bus
import com.movesmart.demo.repository.BusRepository
import org.springframework.stereotype.Service

@Service
class BusService(private val busRepository: BusRepository) {
    fun createBus(bus: Bus): Bus = busRepository.save(bus)
    fun getAllBuses(): List<Bus> = busRepository.findAll()
}
