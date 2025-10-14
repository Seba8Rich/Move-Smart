package com.movesmart.demo.model


import jakarta.persistence.*

@Entity
data class Organization(
    @Id
    val id: Long = 1,
    val name: String,
    val address: String,
    val contactNumber: String,
    val email:String
)


