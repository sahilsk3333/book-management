package me.sahil.book_management.auth.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.sahil.book_management.core.exception.TokenMissingException
import me.sahil.book_management.core.route.ApiRoutes.Companion.UNAUTHENTICATED_ROUTES
import me.sahil.book_management.core.utils.extractBearerToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val requestPath = request.requestURI

        // Skip token validation for unauthenticated routes
        if (UNAUTHENTICATED_ROUTES.any { route -> requestPath.matchesRoute(route) }) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val token = resolveToken(request)

            // Validate the token
            if (token != null && jwtTokenProvider.validateToken(token)) {
                val authentication = jwtTokenProvider.getAuthentication(token)

                // Set the authentication in the context
                SecurityContextHolder.getContext().authentication = authentication
            } else if (token == null) {
                throw TokenMissingException("Login is required")
            }

            filterChain.doFilter(request, response)

        } catch (ex: TokenMissingException) {
            handleException(response = response, message = ex.message)
        } catch (ex: Exception) {
            handleException(response = response, message = "Invalid Token")
        }
    }

    private fun handleException(
        response: HttpServletResponse,
        status: Int = HttpServletResponse.SC_UNAUTHORIZED,
        message: String?
    ) {
        response.status = status
        response.contentType = "application/json"
        response.writer.write(
            """{"error": "$message"}"""
        )
    }

    private fun String.matchesRoute(pattern: String): Boolean {
        val matcher = org.springframework.util.AntPathMatcher()
        return matcher.match(pattern, this)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization") ?: return null
        return if (bearerToken.startsWith("Bearer ")) {
            bearerToken.extractBearerToken()
        } else null
    }
}
