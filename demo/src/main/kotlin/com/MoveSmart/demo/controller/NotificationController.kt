package com.movesmart.demo.controller

import com.movesmart.demo.model.Notification
import com.movesmart.demo.service.NotificationService
import com.movesmart.demo.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService,
    private val userService: UserService
) {

    @GetMapping
    fun getNotifications(authentication: Authentication): ResponseEntity<List<Notification>> {
        val username = authentication.name
        val user = userService.findByEmailOrPhone(username)
        val notifications = notificationService.getUserNotifications(user)
        return ResponseEntity.ok(notifications)
    }

    @GetMapping("/unread")
    fun getUnreadNotifications(authentication: Authentication): ResponseEntity<List<Notification>> {
        val username = authentication.name
        val user = userService.findByEmailOrPhone(username)
        val notifications = notificationService.getUnreadNotifications(user)
        return ResponseEntity.ok(notifications)
    }

    @GetMapping("/unread/count")
    fun getUnreadCount(authentication: Authentication): ResponseEntity<Map<String, Long>> {
        val username = authentication.name
        val user = userService.findByEmailOrPhone(username)
        val count = notificationService.getUnreadCount(user)
        return ResponseEntity.ok(mapOf("count" to count))
    }

    @PutMapping("/{id}/read")
    fun markAsRead(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<Any> {
        return try {
            val username = authentication.name
            val user = userService.findByEmailOrPhone(username)
            val notification = notificationService.markAsRead(id, user)
            ResponseEntity.ok(notification)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("message" to (ex.message ?: "Failed to mark notification as read")))
        }
    }

    @PutMapping("/read-all")
    fun markAllAsRead(authentication: Authentication): ResponseEntity<Map<String, String>> {
        val username = authentication.name
        val user = userService.findByEmailOrPhone(username)
        notificationService.markAllAsRead(user)
        return ResponseEntity.ok(mapOf("message" to "All notifications marked as read"))
    }
}

