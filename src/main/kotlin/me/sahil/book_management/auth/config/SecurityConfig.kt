package me.sahil.book_management.auth.config

import me.sahil.book_management.auth.security.JwtAuthenticationFilter
import me.sahil.book_management.auth.security.JwtTokenProvider
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

@Configuration
@EnableWebSecurity
class SecurityConfig(private val jwtTokenProvider: JwtTokenProvider) :
    SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>() {

    @Bean
    fun securityFilterChain(http: HttpSecurity): DefaultSecurityFilterChain {
        return http
            .csrf { csrf -> csrf.disable() } // Disable CSRF for testing purposes
            .authorizeHttpRequests { authz ->
                authz.requestMatchers("api/auth/register", "api/auth/login","/api/files/download/**")
                    .permitAll()// Allow registration and login without authentication
                    .anyRequest().authenticated() // Secure all other endpoints
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session for JWT
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