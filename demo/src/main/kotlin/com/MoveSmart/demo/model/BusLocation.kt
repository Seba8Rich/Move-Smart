package com.movesmart.demo.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "bus_locations")
data class BusLocation(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    val bus: Bus? = null,

    @Column(nullable = false)
    val latitude: Double = 0.0,

    @Column(nullable = false)
    val longitude: Double = 0.0,

    val timestamp: LocalDateTime = LocalDateTime.now()
)
