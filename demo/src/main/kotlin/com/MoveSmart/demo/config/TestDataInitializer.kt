package com.movesmart.demo.config

import com.movesmart.demo.model.User
import com.movesmart.demo.model.UserRole
import com.movesmart.demo.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

/**
 * Initializes test data on application startup.
 * Creates a test user for Android app testing if it doesn't already exist.
 */
@Component
class TestDataInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(TestDataInitializer::class.java)

    override fun run(vararg args: String?) {
        createTestUser()
    }

    private fun createTestUser() {
        val testEmail = "test@test.com"
        val testPhone = "+1234567890" // Default test phone number
        
        // Check if test user already exists by email
        val existingUserByEmail = userRepository.findByUserEmail(testEmail)
        if (existingUserByEmail != null) {
            logger.info("Test user with email '$testEmail' already exists. Skipping creation.")
            return
        }

        // Check if test phone number is already taken
        val existingUserByPhone = userRepository.findByUserPhoneNumber(testPhone)
        if (existingUserByPhone != null) {
            logger.warn("Phone number '$testPhone' is already taken by another user. Using alternative phone number.")
            // Try alternative phone numbers
            var phoneNumber = testPhone
            var counter = 1
            while (userRepository.findByUserPhoneNumber(phoneNumber) != null && counter < 10) {
                phoneNumber = "+123456789$counter"
                counter++
            }
            if (userRepository.findByUserPhoneNumber(phoneNumber) != null) {
                logger.error("Could not create test user: All test phone numbers are taken.")
                return
            }
            createUserWithPhone(testEmail, phoneNumber)
        } else {
            createUserWithPhone(testEmail, testPhone)
        }
    }

    private fun createUserWithPhone(email: String, phoneNumber: String) {
        try {
            val testPassword = "test"
            val hashedPassword = passwordEncoder.encode(testPassword)

            val testUser = User(
                userName = "Test User",
                userEmail = email,
                userPhoneNumber = phoneNumber,
                userPassword = hashedPassword,
                userRole = UserRole.PASSENGER
            )

            val savedUser = userRepository.save(testUser)
            logger.info("Test user created successfully!")
            logger.info("Email: $email")
            logger.info("Password: test")
            logger.info("Phone: $phoneNumber")
            logger.info("Role: PASSENGER")
            logger.info("User ID: ${savedUser.userId}")
        } catch (e: Exception) {
            logger.error("Failed to create test user: ${e.message}", e)
        }
    }
}

