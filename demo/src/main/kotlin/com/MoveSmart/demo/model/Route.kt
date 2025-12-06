package com.movesmart.demo.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
@Table(name = "routes")
@JsonIgnoreProperties(value = ["hibernateLazyInitializer", "handler"], allowGetters = true)
data class Route(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "route_id", nullable = true, unique = true)
    val routeId: Long? = null, // User-defined display ID (optional)

    @Column(nullable = false)
    val startStation: String = "",

    @Column(nullable = false)
    val endStation: String = "",

    val distanceKm: Double = 0.0,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "route_bus",
        joinColumns = [JoinColumn(name = "route_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "bus_id")]
    )
    @JsonIgnoreProperties(value = ["organization", "hibernateLazyInitializer", "handler", "routes"])
    val buses: MutableSet<Bus> = mutableSetOf()
)
