package com.movesmart.demo.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
@JsonIgnoreProperties(value = ["hibernateLazyInitializer", "handler"], allowGetters = true)
data class Bus(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val plateNumber: String,
    val capacity: Int,
    val route: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @JsonIgnoreProperties(value = ["buses", "hibernateLazyInitializer", "handler"])
    val organization: Organization
)
