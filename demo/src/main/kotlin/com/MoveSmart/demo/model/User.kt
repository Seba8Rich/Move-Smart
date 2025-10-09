package com.movesmart.demo.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userId: Long = 0,

    @Column(nullable = false)
    val userName: String = "",

    @Column(unique = true, nullable = true)
    val userEmail: String = "",

    @Column(unique = true, nullable = false)
    val userPhoneNumber: String = "",

    @Column(nullable = false)
    val userPassword: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var userRole: UserRole
)

enum class UserRole {
    PASSENGER,
    DRIVER
}
