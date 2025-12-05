package com.movesmart.demo.repository

import com.movesmart.demo.model.User
import com.movesmart.demo.model.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUserEmail(userEmail: String): User?
    fun findByUserPhoneNumber(userPhoneNumber: String): User?
    fun existsByUserEmail(userEmail: String): Boolean
    fun existsByUserPhoneNumber(userPhoneNumber: String): Boolean
    fun findByUserRole(userRole: UserRole): List<User>
}
