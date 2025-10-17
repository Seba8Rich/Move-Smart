package com.movesmart.demo.model


import jakarta.persistence.*

@Entity
@Table(name = "organizations")
data class Organization(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val name: String,
    val address: String,
    val contactNumber: String,
    val email: String
)


