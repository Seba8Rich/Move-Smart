package com.movesmart.demo.model

import jakarta.persistence.*

@Entity
@Table(name = "routes")
data class Route(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val startStation: String = "",

    @Column(nullable = false)
    val endStation: String = "",

    val distanceKm: Double = 0.0,

    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    val bus: Bus? = null
)
