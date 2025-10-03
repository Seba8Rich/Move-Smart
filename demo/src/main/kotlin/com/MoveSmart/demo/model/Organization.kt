package com.movesmart.demo.model


import jakarta.persistence.*

@Entity
data class Organization(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val address: String,
    val contactNumber: String
)


