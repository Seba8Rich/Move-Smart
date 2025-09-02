package com.MoveSmart.demo.repository


import com.movesmart.demo.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long>
