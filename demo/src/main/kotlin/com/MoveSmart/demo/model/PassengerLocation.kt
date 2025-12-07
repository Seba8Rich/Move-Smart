package com.movesmart.demo.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "passenger_locations")
@JsonIgnoreProperties(value = ["hibernateLazyInitializer", "handler"], allowGetters = true)
data class PassengerLocation(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    @JsonIgnoreProperties("userPassword")
    val passenger: User,

    @Column(nullable = false)
    val latitude: Double,

    @Column(nullable = false)
    val longitude: Double,

    @Column(nullable = false)
    val recordedAt: LocalDateTime = LocalDateTime.now()
)
