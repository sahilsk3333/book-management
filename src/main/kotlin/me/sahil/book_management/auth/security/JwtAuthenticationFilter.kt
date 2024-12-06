package me.sahil.book_management.auth.security

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.sahil.book_management.exception.TokenExpiredException
import me.sahil.book_management.exception.TokenInvalidException
import me.sahil.book_management.exception.TokenMissingException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw TokenMissingException("Authorization token is missing")
        }

        val token = authHeader.removePrefix("Bearer ")

        try {
            if (!jwtTokenProvider.validateToken(token)) {
                throw TokenInvalidException("Invalid token")
            }

            val userDetails = jwtTokenProvider.getUserDetailsFromToken(token)
            val authentication = UsernamePasswordAuthenticationToken(userDetails, null, emptyList())
            SecurityContextHolder.getContext().authentication = authentication
        } catch (e: ExpiredJwtException) {
            throw TokenExpiredException("Token has expired")
        } catch (e: MalformedJwtException) {
            throw TokenInvalidException("Malformed token")
        } catch (e: Exception) {
            throw TokenInvalidException("Invalid token")
        }

        chain.doFilter(request, response)
    }
}
