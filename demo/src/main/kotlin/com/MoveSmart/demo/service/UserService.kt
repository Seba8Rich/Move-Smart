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
        // Check email uniqueness only if email is provided
        if (user.userEmail.isNotBlank() && userRepository.findByUserEmail(user.userEmail) != null) {
            throw IllegalArgumentException("Email already registered")
        }

        // Check phone number uniqueness
        if (userRepository.findByUserPhoneNumber(user.userPhoneNumber) != null) {
            throw IllegalArgumentException("Phone number already registered")
        }

        val hashedPassword = passwordEncoder.encode(user.userPassword)
        val userWithHashedPassword = user.copy(userPassword = hashedPassword)

        return userRepository.save(userWithHashedPassword)
    }

    fun getAllUsers(): List<User> = userRepository.findAll()

    fun getUserById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found with ID: $id") }
    }

    fun updateUser(id: Long, user: User): User {
        val existingUser = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found with ID: $id") }


        if (user.userEmail != existingUser.userEmail &&
            userRepository.findByUserEmail(user.userEmail) != null
        ) {
            throw IllegalArgumentException("Email already registered by another user")
        }


        val hashedPassword = if (user.userPassword.isNotBlank()) {
            if (user.userPassword.startsWith("$2a$") || user.userPassword.startsWith("$2b$")) {
                user.userPassword
            } else {
                passwordEncoder.encode(user.userPassword)
            }
        } else {
            existingUser.userPassword
        }

        val updatedUser = existingUser.copy(
            userEmail = user.userEmail,
            userPhoneNumber = user.userPhoneNumber,
            userPassword = hashedPassword
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
        val user = userRepository.findByUserEmail(identifier)
            ?: userRepository.findByUserPhoneNumber(identifier)

        return user ?: throw IllegalArgumentException("User not found with email or phone: $identifier")
    }

    fun changePassword(userId: Long, oldPassword: String, newPassword: String): User {
        val user = getUserById(userId)
        
        if (!passwordEncoder.matches(oldPassword, user.userPassword)) {
            throw IllegalArgumentException("Current password is incorrect")
        }
        
        if (newPassword.length < 6) {
            throw IllegalArgumentException("New password must be at least 6 characters long")
        }
        
        val hashedPassword = passwordEncoder.encode(newPassword)
        val updatedUser = user.copy(userPassword = hashedPassword)
        
        return userRepository.save(updatedUser)
    }

    fun updateCurrentUserProfile(username: String, userName: String?, userEmail: String?, userPhoneNumber: String?): User {
        val user = findByEmailOrPhone(username)
        
        val updatedUser = user.copy(
            userName = userName ?: user.userName,
            userEmail = userEmail?.trim()?.takeIf { it.isNotBlank() } ?: user.userEmail,
            userPhoneNumber = userPhoneNumber?.trim() ?: user.userPhoneNumber
        )
        
        // Check email uniqueness if changed
        if (updatedUser.userEmail != user.userEmail && 
            updatedUser.userEmail.isNotBlank() &&
            userRepository.findByUserEmail(updatedUser.userEmail) != null) {
            throw IllegalArgumentException("Email already registered by another user")
        }
        
        // Check phone uniqueness if changed
        if (updatedUser.userPhoneNumber != user.userPhoneNumber &&
            userRepository.findByUserPhoneNumber(updatedUser.userPhoneNumber) != null) {
            throw IllegalArgumentException("Phone number already registered by another user")
        }
        
        return userRepository.save(updatedUser)
    }
}
