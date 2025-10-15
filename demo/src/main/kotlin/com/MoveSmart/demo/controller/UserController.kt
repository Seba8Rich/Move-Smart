package com.movesmart.demo.controller

import com.movesmart.demo.model.User
import com.movesmart.demo.service.UserService
import org.springframework.http.ResponseEntity
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
    
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): User = userService.getUserById(id)
    
    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody user: User): User = userService.updateUser(id, user)
    
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<String> {
        return if (userService.deleteUser(id)) {
            ResponseEntity.ok("User deleted successfully")
        } else {
            ResponseEntity.notFound().build()
        }
    }
}


