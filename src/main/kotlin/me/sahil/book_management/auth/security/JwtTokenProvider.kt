package me.sahil.book_management.auth.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import me.sahil.book_management.core.exception.TokenInvalidException
import me.sahil.book_management.user.entity.User
import me.sahil.book_management.core.role.Role
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey


/**
 * A component responsible for generating, validating, and extracting user information from JWT tokens.
 *
 * This provider handles the creation of JWT tokens with user-specific claims, the validation of JWT tokens,
 * and the extraction of user details from a token. It uses the HS384 algorithm and a secret key to ensure the security
 * of the tokens. The generated tokens are valid for 1 hour.
 *
 * @property secretKey The secret key used for signing and verifying JWT tokens. It is injected from the application properties.
 * @property validityInMilliseconds The validity duration of the token, set to 1 hour (3600000 milliseconds).
 */
@Component
class JwtTokenProvider {

    @Value("\${jwt.secret-key}")
    private lateinit var secretKey: String

    private val validityInMilliseconds: Long = 3600000 // 1 hour

    /**
     * Data class to hold user claims securely. These claims are embedded in the JWT token.
     *
     * @property id The unique identifier of the user.
     * @property email The user's email address.
     * @property name The user's name.
     * @property role The role assigned to the user.
     */
    data class UserClaims(val id: Long, val email: String, val name: String, val role: Role)

    /**
     * Generates a JWT token for the given user with claims such as `id`, `email`, `name`, and `role`.
     *
     * The token is signed using the HS384 algorithm and the secret key. The validity of the token is set to 1 hour.
     *
     * @param user The user for whom the token is being generated.
     * @return A JWT token as a String.
     */
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

    /**
     * Extracts user details from a JWT token, including the user's `id`, `email`, `name`, and `role`.
     *
     * This method parses the token and returns a `UserClaims` object containing the extracted user details.
     *
     * @param token The JWT token from which user details are extracted.
     * @return A `UserClaims` object containing the extracted user details.
     * @throws Exception If the token is invalid or expired.
     */
    fun getUserDetailsFromToken(token: String): UserClaims {
        try {
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
        } catch (e: JwtException) {
            throw TokenInvalidException("Invalid or expired token")
        }
    }

    /**
     * Validates a JWT token by checking its signature and expiration date.
     *
     * This method ensures the token is correctly signed and has not expired.
     *
     * @param token The JWT token to be validated.
     * @return `true` if the token is valid, `false` otherwise.
     */
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

    /**
     * Generates an `Authentication` object from the JWT token.
     *
     * This method extracts user details from the token and creates an authentication token for Spring Security.
     * The user's role is converted into a list of authorities, which is used to grant access based on roles.
     *
     * @param token The JWT token to extract user details from.
     * @return An `Authentication` object representing the user.
     */
    fun getAuthentication(token: String): Authentication {
        val userClaims = getUserDetailsFromToken(token)

        // Create the authentication token using the userClaims directly
        val authorities = listOf(SimpleGrantedAuthority("ROLE_${userClaims.role.name}"))

        // You can use the UsernamePasswordAuthenticationToken here instead of creating a UserPrincipal
        return UsernamePasswordAuthenticationToken(userClaims.email, token, authorities)
    }

    /**
     * Checks if the provided token has expired.
     *
     * This method checks the expiration date of the token and compares it with the current date.
     *
     * @param token The JWT token to check for expiration.
     * @return `true` if the token has expired, `false` otherwise.
     */
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

