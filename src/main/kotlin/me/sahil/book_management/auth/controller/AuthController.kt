package me.sahil.book_management.auth.controller

import jakarta.validation.Valid
import me.sahil.book_management.auth.dto.LoginRequest
import me.sahil.book_management.auth.dto.RegisterRequest
import me.sahil.book_management.auth.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody registerRequest: RegisterRequest, result: BindingResult): ResponseEntity<Any> {
        if (result.hasErrors()) {
            val errors = result.allErrors.map { (it as FieldError).defaultMessage }.joinToString(", ")
            return ResponseEntity.badRequest().body(mapOf("error" to "Validation failed", "message" to errors))
        }

        return try {
            val response = authService.register(registerRequest)
            val (user, message) = response
            ResponseEntity.status(HttpStatus.CREATED).body(mapOf("message" to message, "user" to user))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to "Bad Request", "message" to ex.message))
        }
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest, result: BindingResult): ResponseEntity<Any> {
        if (result.hasErrors()) {
            val errors = result.allErrors.map { (it as FieldError).defaultMessage }.joinToString(", ")
            return ResponseEntity.badRequest().body(mapOf("error" to "Validation failed", "message" to errors))
        }

        return try {
            val response = authService.login(loginRequest)
            val (user, token) = response
            ResponseEntity.ok(mapOf("token" to token, "user" to user))
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Unauthorized", "message" to ex.message))
        }
    }
}

