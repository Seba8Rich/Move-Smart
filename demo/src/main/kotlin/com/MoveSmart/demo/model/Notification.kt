package com.movesmart.demo.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "notifications")
data class Notification(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val notificationId: Long? = null,

    @Column(nullable = false)
    var title: String = "",

    @Column(nullable = false, length = 1000)
    var message: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: NotificationType = NotificationType.INFO,

    @Column(nullable = false)
    var isRead: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    var user: User? = null, // null means system-wide notification

    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)

enum class NotificationType {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}

