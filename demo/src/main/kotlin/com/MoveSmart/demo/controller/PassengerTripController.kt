package com.movesmart.demo.controller

import com.movesmart.demo.model.PassengerTrip
import com.movesmart.demo.service.PassengerTripService
import org.springframework.web.bind.annotation.RequestBody

class PassengerTripController (private val passengerTripService: PassengerTripService){

    fun createPassengerTrip(@RequestBody passengerTrip: PassengerTrip): PassengerTrip{
        return passengerTripService.createPassengerTrip(passengerTrip)
    }
}