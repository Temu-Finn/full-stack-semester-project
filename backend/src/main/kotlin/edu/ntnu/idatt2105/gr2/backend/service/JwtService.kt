package edu.ntnu.idatt2105.gr2.backend.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey


@Service
class JwtService {
    @Value("\${security.jwt.secret-key}")
    private val secret: String? = null

    @Value("\${security.jwt.expiration-time}")
    private var expiration: Long = 0

    fun generateToken(username: String): String {
        val now = System.currentTimeMillis();
        val expirationTime = now + expiration;

        return Jwts.builder()
            .subject(username)
            .issuedAt(Date(now))
            .expiration(Date(expirationTime))
            .signWith(getSignInKey())
            .compact()
    }

    fun extractUsernameFromToken(token: String): String? {
        val claims = getAllClaimsFromToken(token)
        return claims.subject?.toString()
    }

    fun isTokenValid(token: String, username: String): Boolean {
        return username == extractUsernameFromToken(token) && !isTokenExpired(token)
    }

    fun getExpirationTime(): Long {
        return expiration
    }

    private fun isTokenExpired(token: String): Boolean {
        val expirationDate = getAllClaimsFromToken(token).expiration
        val now = Date()

        return now.after(expirationDate)
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

    private fun getSignInKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(secret)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}