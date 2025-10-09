package com.movesmart.demo.model

import jakarta.persistence.*

@Entity
data class Bus(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val plateNumber: String,
    val capacity: Int,
    val route: String,

    @ManyToOne(fetch = FetchType.LAZY)
    val organization: Organization
)
