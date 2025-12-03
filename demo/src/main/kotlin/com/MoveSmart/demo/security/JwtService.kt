package com.movesmart.demo.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${security.jwt.secret:replace-with-32+chars-secret-key-please-update}") private val secret: String,
    @Value("\${security.jwt.expirationMs:86400000}") private val expirationMs: Long
) {
    private val key: SecretKey by lazy { 
        if (secret.length < 32) {
            throw IllegalStateException("JWT secret must be at least 32 characters long. Current length: ${secret.length}")
        }
        Keys.hmacShaKeyFor(secret.toByteArray()) 
    }

    fun generateToken(username: String, claims: Map<String, Any> = emptyMap()): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationMs)

        val builder = Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS256)

        claims.forEach { (key, value) -> builder.claim(key, value) }

        return builder.compact()
    }

    fun extractUsername(token: String): String {
        return extractClaims(token).subject
    }

    fun isTokenValid(token: String, username: String): Boolean {
        val claims = extractClaims(token)
        return claims.subject == username && !isTokenExpired(claims)
    }

    private fun extractClaims(token: String): Claims {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid JWT token", e)
        }
    }

    private fun isTokenExpired(claims: Claims): Boolean {
        return claims.expiration.before(Date())
    }
}