package com.movesmart.demo.controller

import com.movesmart.demo.model.User
import com.movesmart.demo.service.UserService
import org.springframework.web.bind.annotation.RequestBody

class UserController (private val userService: UserService){

    fun createUser(@RequestBody user: User): User{
        return userService.createUser(user)
    }
}