package com.movesmart.demo.controller

import com.movesmart.demo.model.User
import com.movesmart.demo.service.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun createUser(@RequestBody user: User): User  {
        return userService.createUser(user)
    }

    @GetMapping
    fun getAllUsers(): List<User> = userService.getAllUsers()
    }


