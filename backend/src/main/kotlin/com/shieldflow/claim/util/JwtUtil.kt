package com.shieldflow.claim.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtUtil {
    private val secret = "shieldflow_jwt_secret_key_secure_2026_abc_123"
    private val algorithm = Algorithm.HMAC256(secret)
    private val validityMillis = 24 * 60 * 60 * 1000L // 24 hours

    fun generateToken(id: Long, email: String, role: String, fullName: String): String {
        val now = Date()
        val validity = Date(now.time + validityMillis)

        return JWT.create()
            .withSubject(email)
            .withClaim("userId", id)
            .withClaim("role", role)
            .withClaim("fullName", fullName)
            .withIssuedAt(now)
            .withExpiresAt(validity)
            .sign(algorithm)
    }

    fun verifyToken(token: String): DecodedJwtInfo? {
        return try {
            val verifier = JWT.require(algorithm).build()
            val decodedJWT: DecodedJWT = verifier.verify(token)
            
            val email = decodedJWT.subject
            val userId = decodedJWT.getClaim("userId").asLong()
            val role = decodedJWT.getClaim("role").asString()
            val fullName = decodedJWT.getClaim("fullName").asString()

            DecodedJwtInfo(userId, email, role, fullName)
        } catch (e: Exception) {
            null
        }
    }
}

data class DecodedJwtInfo(
    val userId: Long,
    val email: String,
    val role: String,
    val fullName: String
)
