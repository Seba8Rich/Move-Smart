package com.movesmart.demo.security

import com.movesmart.demo.repository.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {

        val entity = userRepository.findByUserEmail(username)
            ?: userRepository.findByUserPhoneNumber(username)
            ?: throw UsernameNotFoundException("User not found: $username")

        val authorities: List<GrantedAuthority> = listOf(SimpleGrantedAuthority("ROLE_${entity.userRole.name}"))
        return User(entity.userEmail.ifBlank { entity.userPhoneNumber }, entity.userPassword, authorities)
    }
}
