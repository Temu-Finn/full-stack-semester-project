package edu.ntnu.idatt2105.gr2.backend.service

import edu.ntnu.idatt2105.gr2.backend.model.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey


@Service
class JwtService {
    @Value("\${security.jwt.secret-key}")
    private val secret: String? = null

    @Value("\${security.jwt.expiration-time}")
    private var expiration: Long = 0

    fun generateToken(userDetails: UserDetails): String {
        val now = System.currentTimeMillis();
        val expirationTime = now + expiration;

        val userId = userDetails.username

        return Jwts.builder()
            .subject(userId)
            .issuedAt(Date(now))
            .expiration(Date(expirationTime))
            .signWith(getSignInKey())
            .compact()
    }

    fun extractUserIdFromToken(token: String): Int? {
        val claims = getAllClaimsFromToken(token)
        return claims.subject?.toIntOrNull()
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val extractedUserId: Int? = extractUserIdFromToken(token)
        val userIdFromUserDetails: Int? = userDetails.username.toIntOrNull()
        return extractedUserId != null && extractedUserId == userIdFromUserDetails && !isTokenExpired(token)
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