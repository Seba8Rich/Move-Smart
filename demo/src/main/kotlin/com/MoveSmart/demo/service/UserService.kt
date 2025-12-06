package com.movesmart.demo.service


import com.movesmart.demo.model.User
import com.movesmart.demo.model.UserRole
import com.movesmart.demo.repository.BusRepository
import com.movesmart.demo.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val busRepository: BusRepository
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

    fun getUsersByRole(userRole: UserRole): List<User> {
        return userRepository.findByUserRole(userRole)
    }

    fun getUserById(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found with ID: $id") }
    }

    fun updateUser(
        id: Long,
        userName: String? = null,
        userEmail: String? = null,
        userPhoneNumber: String? = null,
        userPassword: String? = null,
        userRole: String? = null
    ): User {
        val existingUser = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found with ID: $id") }

        // Validate email uniqueness if email is being changed
        val newEmail = userEmail?.trim()?.takeIf { it.isNotBlank() } ?: existingUser.userEmail
        if (newEmail != existingUser.userEmail &&
            userRepository.findByUserEmail(newEmail) != null
        ) {
            throw IllegalArgumentException("Email already registered by another user")
        }

        // Validate phone number uniqueness if phone is being changed
        val newPhoneNumber = userPhoneNumber?.trim() ?: existingUser.userPhoneNumber
        if (newPhoneNumber != existingUser.userPhoneNumber &&
            userRepository.findByUserPhoneNumber(newPhoneNumber) != null
        ) {
            throw IllegalArgumentException("Phone number already registered by another user")
        }

        // Handle password hashing
        val hashedPassword = if (userPassword != null && userPassword.isNotBlank()) {
            if (userPassword.startsWith("$2a$") || userPassword.startsWith("$2b$")) {
                userPassword // Already hashed
            } else {
                passwordEncoder.encode(userPassword) // Hash the new password
            }
        } else {
            existingUser.userPassword // Keep existing password
        }

        // Parse userRole if provided
        val newUserRole = if (userRole != null && userRole.isNotBlank()) {
            try {
                UserRole.valueOf(userRole.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid user role: $userRole. Valid roles are: ${UserRole.values().joinToString { it.name }}")
            }
        } else {
            existingUser.userRole
        }

        val updatedUser = existingUser.copy(
            userName = userName?.trim()?.takeIf { it.isNotBlank() } ?: existingUser.userName,
            userEmail = newEmail,
            userPhoneNumber = newPhoneNumber,
            userPassword = hashedPassword,
            userRole = newUserRole
        )

        return userRepository.save(updatedUser)
    }

    fun deleteUser(id: Long): Boolean {
        if (!userRepository.existsById(id)) {
            return false
        }
        
        // Check if user is assigned as driver to any bus
        val busesWithDriver = busRepository.findByDriverId(id)
        if (busesWithDriver.isNotEmpty()) {
            // Unassign the user from all buses before deletion
            busRepository.unassignDriverFromAllBuses(id)
        }
        
        // Now safe to delete the user
        userRepository.deleteById(id)
        return true
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
