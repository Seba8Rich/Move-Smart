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
    @Value("\${security.jwt.secret:change}") private val secret: String,
    @Value("\${security.jwt.expirationMs:86400000}") private val expirationMs: Long
) {
    private val key: SecretKey by lazy { Keys.hmacShaKeyFor(secret.toByteArray()) }

    fun generateToken(subject: String, claims: Map<String, Any> = emptyMap()): String {
        val now = Date()
        val expiry = Date(now.time + expirationMs)
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun extractUsername(token: String): String = extractAllClaims(token).subject

    fun isTokenValid(token: String, username: String): Boolean {
        val claims = extractAllClaims(token)
        val notExpired = claims.expiration.after(Date())
        return notExpired && claims.subject == username
    }

    private fun extractAllClaims(token: String): Claims =
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
}


