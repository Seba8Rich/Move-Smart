package com.movesmart.demo.model

import jakarta.persistence.*

@Entity
@Table(name = "organizations")
data class Organization(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val name: String = "",

    val address: String = ""
)
