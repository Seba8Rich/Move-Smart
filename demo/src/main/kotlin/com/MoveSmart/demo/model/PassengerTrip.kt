package com.movesmart.demo.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "passenger_trips")
data class PassengerTrip(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "passenger_id", nullable = false)
    val passenger: User? = null,

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    val route: Route? = null,

    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    val bus: Bus? = null,

    @Column(nullable = false)
    val startStation: String = "",

    @Column(nullable = false)
    val endStation: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tripStatus: TripStatus = TripStatus.BOOKED,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class TripStatus {
    BOOKED, ONGOING, COMPLETED, CANCELLED
}
