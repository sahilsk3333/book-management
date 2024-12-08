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

/**
 * A custom filter that intercepts HTTP requests to validate JWT tokens for authentication.
 *
 * This filter ensures that only authenticated users can access routes that require authentication.
 * It skips token validation for unauthenticated routes and applies token validation to others.
 * If a valid token is provided, the user's authentication is set in the Spring Security context.
 * If no token is provided or the token is invalid, an error response with a 401 Unauthorized status is sent.
 *
 * @property jwtTokenProvider A provider that is responsible for JWT token validation and user authentication.
 */
@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    /**
     * Filters the incoming HTTP request to validate the JWT token if required.
     *
     * The filter performs the following checks:
     * - If the request's URI matches any unauthenticated route, it bypasses token validation.
     * - If the token is valid, it sets the authentication in the Spring Security context.
     * - If the token is missing or invalid, it sends a 401 Unauthorized response with an error message.
     *
     * @param request The HTTP request to be filtered.
     * @param response The HTTP response to be written to if the request is unauthorized or invalid.
     * @param filterChain The filter chain that continues the request processing.
     */
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

    /**
     * Handles exceptions by sending an error response to the client.
     *
     * This method sets the response status to `401 Unauthorized` and writes a JSON error message to the response body.
     *
     * @param response The HTTP response to send the error to.
     * @param status The HTTP status code to be set (defaults to `401 Unauthorized`).
     * @param message The error message to be included in the response body.
     */
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

    /**
     * Matches the request path against a pattern to determine if it matches an unauthenticated route.
     *
     * @param pattern The route pattern to match the request URI against.
     * @return `true` if the request URI matches the pattern, `false` otherwise.
     */
    private fun String.matchesRoute(pattern: String): Boolean {
        val matcher = org.springframework.util.AntPathMatcher()
        return matcher.match(pattern, this)
    }

    /**
     * Resolves the JWT token from the `Authorization` header of the HTTP request.
     *
     * The token must be prefixed with "Bearer " to be valid. If the header contains a valid token,
     * it is extracted and returned.
     *
     * @param request The HTTP request from which the token is to be extracted.
     * @return The JWT token if present and valid, `null` otherwise.
     */
    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization") ?: return null
        return if (bearerToken.startsWith("Bearer ")) {
            bearerToken.extractBearerToken()
        } else null
    }
}
