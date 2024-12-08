package me.sahil.book_management.auth.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import me.sahil.book_management.user.entity.User
import me.sahil.book_management.core.role.Role
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtTokenProvider {

    @Value("\${jwt.secret-key}")
    private lateinit var secretKey: String

    private val validityInMilliseconds: Long = 3600000 // 1 hour

    // Data class to hold claims securely
    data class UserClaims(val id: Long, val email: String, val name: String, val role: Role)

    // Generate a JWT token with HS384 algorithm
    fun generateToken(user: User): String {
        val claims = Jwts.claims().apply {
            put("id", user.id)
            put("email", user.email)
            put("name", user.name)
            put("role", user.role.name)
        }
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        val signingKey: SecretKey = Keys.hmacShaKeyFor(secretKey.toByteArray())

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(signingKey, SignatureAlgorithm.HS384)
            .compact()
    }

    // Extract user claims from the token (type-safe)
    fun getUserDetailsFromToken(token: String): UserClaims {
        val claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .build()
            .parseClaimsJws(token)
            .body

        return UserClaims(
            id = (claims["id"] as Int).toLong(),
            email = claims["email"] as String,
            name = claims["name"] as String,
            role = Role.valueOf(claims["role"] as String)
        )
    }

    // Validate the JWT token with HS384 algorithm
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.toByteArray()))
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getAuthentication(token: String): Authentication {
        val userClaims = getUserDetailsFromToken(token)

        // Create the authentication token using the userClaims directly
        val authorities = listOf(SimpleGrantedAuthority("ROLE_${userClaims.role.name}"))

        // You can use the UsernamePasswordAuthenticationToken here instead of creating a UserPrincipal
        return UsernamePasswordAuthenticationToken(userClaims.email, token, authorities)
    }



    // Check if the token has expired
    fun isTokenExpired(token: String): Boolean {
        return try {
            val expiration = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.toByteArray()))
                .build()
                .parseClaimsJws(token)
                .body
                .expiration
            expiration.before(Date())
        } catch (e: Exception) {
            false
        }
    }
}


