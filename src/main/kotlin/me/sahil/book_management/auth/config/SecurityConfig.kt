package me.sahil.book_management.auth.config

import jakarta.servlet.http.HttpServletResponse
import me.sahil.book_management.auth.security.JwtAuthenticationFilter
import me.sahil.book_management.auth.security.JwtTokenProvider
import me.sahil.book_management.core.route.ApiRoutes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.OPTIONS
import org.springframework.security.config.annotation.SecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * Security configuration class for setting up JWT-based authentication and authorization.
 *
 * This class configures the security filter chain for the application, disables CSRF protection
 * for testing purposes, sets up session management to be stateless (for JWT usage), and handles
 * authentication exceptions by returning a JSON error message with a 401 Unauthorized status.
 *
 * It also specifies which routes should be publicly accessible (unauthenticated) and which require
 * authentication.
 *
 * @param jwtTokenProvider The provider responsible for creating and validating JWT tokens.
 */
@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtTokenProvider: JwtTokenProvider) :
    SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    @Bean
    fun securityFilterChain(http: HttpSecurity): DefaultSecurityFilterChain {
        return http
            .csrf { csrf -> csrf.disable() } // Disable CSRF for testing purposes
            .authorizeHttpRequests { authz ->
                authz.requestMatchers(
                   *ApiRoutes.UNAUTHENTICATED_ROUTES
                ).permitAll().anyRequest().authenticated()
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session for JWT
            }.exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint { _, response, _ ->
                    response.status = HttpServletResponse.SC_UNAUTHORIZED
                    response.contentType = "application/json"
                    response.writer.write("""{"error": "Unauthorized"}""")
                }
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider = jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .build()
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}