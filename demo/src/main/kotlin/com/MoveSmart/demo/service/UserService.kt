package com.movesmart.demo.service

import com.movesmart.demo.model.User
import com.movesmart.demo.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    fun createUser(user: User): User = userRepository.save(user)


}
