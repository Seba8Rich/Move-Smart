package com.movesmart.demo.service

import com.movesmart.demo.repository.PassengerTripRepository
import com.movesmart.demo.model.PassengerTrip


class PassengerTripService(private  val passengerTripRepository: PassengerTripRepository) {

    fun createPassengerTrip(passengerTrip: PassengerTrip): PassengerTrip =passengerTripRepository.save(passengerTrip)

}