package com.movesmart.demo.controller

import com.movesmart.demo.model.Notification
import com.movesmart.demo.model.User
import com.movesmart.demo.service.NotificationService
import com.movesmart.demo.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService,
    private val userService: UserService
) {

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    fun getNotifications(authentication: Authentication): ResponseEntity<List<Notification>> {
        val user = getCurrentUser(authentication)
        val notifications = notificationService.getUserNotifications(user)
        return ResponseEntity.ok(notifications)
    }

    @GetMapping("/unread")
    @PreAuthorize("isAuthenticated()")
    fun getUnreadNotifications(authentication: Authentication): ResponseEntity<List<Notification>> {
        val user = getCurrentUser(authentication)
        val notifications = notificationService.getUnreadNotifications(user)
        return ResponseEntity.ok(notifications)
    }

    @GetMapping("/unread/count")
    @PreAuthorize("isAuthenticated()")
    fun getUnreadCount(authentication: Authentication): ResponseEntity<Map<String, Long>> {
        val user = getCurrentUser(authentication)
        val count = notificationService.getUnreadCount(user)
        return ResponseEntity.ok(mapOf("count" to count))
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    fun markAsRead(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<Notification> {
        val user = getCurrentUser(authentication)
        val notification = notificationService.markAsRead(id, user)
        return ResponseEntity.ok(notification)
    }

    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    fun markAllAsRead(authentication: Authentication): ResponseEntity<Map<String, String>> {
        val user = getCurrentUser(authentication)
        notificationService.markAllAsRead(user)
        return ResponseEntity.ok(mapOf("message" to "All notifications marked as read"))
    }
    
    private fun getCurrentUser(authentication: Authentication): User {
        val username = authentication.name
        return userService.findByEmailOrPhone(username)
    }
}

