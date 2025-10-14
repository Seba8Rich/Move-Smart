package com.movesmart.demo.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "passenger_trips")
@JsonIgnoreProperties(value = ["hibernateLazyInitializer", "handler"], allowGetters = true)
data class PassengerTrip(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    val passenger: User? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    @JsonIgnoreProperties("bus")
    val route: Route? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    @JsonIgnoreProperties("organization")
    val bus: Bus? = null,

    @Column(nullable = false)
    val startStation: String = "",

    @Column(nullable = false)
    val endStation: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val tripStatus: TripStatus = TripStatus.BOOKED,

    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PrePersist
    fun onCreate() {
        val now = LocalDateTime.now()

    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun getPassengerId(): Long = passenger?.userId ?: 0
    fun getRouteId(): Long = route?.id ?: 0
    fun getBusId(): Long = bus?.id ?: 0
}

enum class TripStatus {
    BOOKED, ONGOING, COMPLETED, CANCELLED
}
