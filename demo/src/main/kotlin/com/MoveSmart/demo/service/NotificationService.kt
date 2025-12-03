package com.movesmart.demo.service

import com.movesmart.demo.model.Notification
import com.movesmart.demo.model.NotificationType
import com.movesmart.demo.model.User
import com.movesmart.demo.repository.NotificationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class NotificationService(
    private val notificationRepository: NotificationRepository
) {
    fun createNotification(
        title: String,
        message: String,
        type: NotificationType = NotificationType.INFO,
        user: User? = null
    ): Notification {
        val notification = Notification(
            title = title,
            message = message,
            type = type,
            user = user
        )
        return notificationRepository.save(notification)
    }

    fun getUserNotifications(user: User?): List<Notification> {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
    }

    fun getUnreadNotifications(user: User?): List<Notification> {
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user)
    }

    fun getUnreadCount(user: User?): Long {
        return notificationRepository.countByUserAndIsReadFalse(user)
    }

    fun markAsRead(notificationId: Long, user: User?): Notification {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { IllegalArgumentException("Notification not found with ID: $notificationId") }
        
        if (notification.user != user) {
            throw IllegalArgumentException("Notification does not belong to this user")
        }
        
        val updatedNotification = notification.copy(isRead = true)
        return notificationRepository.save(updatedNotification)
    }

    fun markAllAsRead(user: User?) {
        val unreadNotifications = getUnreadNotifications(user)
        unreadNotifications.forEach { notification ->
            notificationRepository.save(notification.copy(isRead = true))
        }
    }
}

