package com.movesmart.demo.repository

import com.movesmart.demo.model.Notification
import com.movesmart.demo.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByUserOrderByCreatedAtDesc(user: User?): List<Notification>
    fun findByUserAndIsReadFalseOrderByCreatedAtDesc(user: User?): List<Notification>
    fun countByUserAndIsReadFalse(user: User?): Long
}

