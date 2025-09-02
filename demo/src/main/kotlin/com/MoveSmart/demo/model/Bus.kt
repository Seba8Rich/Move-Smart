package com.movesmart.demo.model


import jakarta.persistence.*

@Entity
@Table(name = "buses")
data class Bus(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val plateNumber: String = "",

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    val organization: Organization? = null,

    @ManyToOne
    @JoinColumn(name = "driver_id")
    val driver: User? = null
)
