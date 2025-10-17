package com.movesmart.demo.service


import com.movesmart.demo.model.User
import com.movesmart.demo.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun createUser(user: User): User {
        val encoded = user.copy(userPassword = passwordEncoder.encode(user.userPassword))
        return userRepository.save(encoded)
    }
    fun getAllUsers(): List<User> = userRepository.findAll()
    
    fun getUserById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found with ID: $id") }
    }
    
    fun updateUser(id: Long, user: User): User {
        val existingUser = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found with ID: $id") }
        
        val updatedUser = existingUser.copy(
            userEmail = user.userEmail,
            userPhoneNumber = user.userPhoneNumber,
            userPassword = passwordEncoder.encode(user.userPassword)
        )
        
        return userRepository.save(updatedUser)
    }
    
    fun deleteUser(id: Long): Boolean {
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            true
        } else {
            false
        }
    }
    
    fun findByEmailOrPhone(identifier: String): User {
        return userRepository.findByUserEmail(identifier)
            ?: userRepository.findByUserPhoneNumber(identifier)
            ?: throw IllegalArgumentException("User not found with identifier: $identifier")
    }
}
