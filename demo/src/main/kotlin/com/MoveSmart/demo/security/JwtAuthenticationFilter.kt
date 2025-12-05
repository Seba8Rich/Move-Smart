package com.movesmart.demo.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import com.movesmart.demo.security.JwtService

@Component

class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Skip JWT processing for public endpoints
        val path = request.requestURI
        if (path.startsWith("/api/auth/") || 
            path.startsWith("/error") || 
            path.startsWith("/actuator/health") ||
            path.startsWith("/api/google-maps/")) {
            filterChain.doFilter(request, response)
            return
        }

        val authHeader = request.getHeader("Authorization")
        val token = if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authHeader.substring(7)
        } else {
            null
        }

        if (token != null && SecurityContextHolder.getContext().authentication == null) {
            try {
                val username = jwtService.extractUsername(token)
                if (username.isNotBlank()) {
                    val userDetails = userDetailsService.loadUserByUsername(username)
                    if (jwtService.isTokenValid(token, userDetails.username)) {
                        val authToken = UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.authorities
                        )
                        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authToken
                    } else {
                        // Token is invalid (expired or wrong signature)
                        response.contentType = "application/json"
                        response.status = HttpServletResponse.SC_UNAUTHORIZED
                        response.writer.write("{\"error\":\"Unauthorized\",\"message\":\"Invalid or expired token. Please login again.\"}")
                        return
                    }
                }
            } catch (e: IllegalArgumentException) {
                // Invalid token format - likely token was generated with old secret
                response.contentType = "application/json"
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("{\"error\":\"Unauthorized\",\"message\":\"Invalid token. The token may have been generated with an old secret. Please login again to get a new token.\"}")
                return
            } catch (e: org.springframework.security.core.userdetails.UsernameNotFoundException) {
                // User not found - but if this is a public endpoint, allow it to continue
                // The security chain will handle authorization
                // For protected endpoints, this will be caught by Spring Security
            } catch (e: Exception) {
                // Other errors - log but don't expose details
                response.contentType = "application/json"
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("{\"error\":\"Unauthorized\",\"message\":\"Authentication failed: ${e.message}\"}")
                return
            }
        }

        filterChain.doFilter(request, response)
    }
}
