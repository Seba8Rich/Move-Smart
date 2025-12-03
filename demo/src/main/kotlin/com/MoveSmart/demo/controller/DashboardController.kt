package com.movesmart.demo.controller

import com.movesmart.demo.model.User
import com.movesmart.demo.service.NotificationService
import com.movesmart.demo.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/dashboard")
class DashboardController(
    private val userService: UserService,
    private val notificationService: NotificationService
) {

    @GetMapping
    fun getDashboard(authentication: Authentication): ResponseEntity<Map<String, Any>> {
        val username = authentication.name
        val user = userService.findByEmailOrPhone(username)
        val unreadCount = notificationService.getUnreadCount(user)
        
        val dashboard = mapOf(
            "user" to mapOf(
                "id" to user.userId,
                "name" to user.userName,
                "email" to user.userEmail,
                "phoneNumber" to user.userPhoneNumber,
                "role" to user.userRole.name
            ),
            "unreadNotifications" to unreadCount,
            "welcomeMessage" to "Welcome to Move Smart Dashboard, ${user.userName}!"
        )
        
        return ResponseEntity.ok(dashboard)
    }
}

