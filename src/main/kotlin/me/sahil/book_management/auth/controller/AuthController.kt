package me.sahil.book_management.auth.controller

import jakarta.validation.Valid
import me.sahil.book_management.auth.dto.LoginRequest
import me.sahil.book_management.auth.dto.RegisterRequest
import me.sahil.book_management.auth.dto.UpdatePasswordRequest
import me.sahil.book_management.auth.service.AuthService
import me.sahil.book_management.core.route.ApiRoutes
import me.sahil.book_management.core.utils.extractBearerToken
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ApiRoutes.AuthRoutes.PATH)
class AuthController(private val authService: AuthService) {

    @PostMapping(ApiRoutes.AuthRoutes.REGISTER)
    fun register(@Valid @RequestBody registerRequest: RegisterRequest): ResponseEntity<Any> {
        val response = authService.register(registerRequest)
        val (user, message) = response
        return ResponseEntity.status(HttpStatus.CREATED).body(mapOf("message" to message, "user" to user))
    }

    @PostMapping(ApiRoutes.AuthRoutes.LOGIN)
    fun login(@Valid @RequestBody loginRequest: LoginRequest, result: BindingResult): ResponseEntity<Any> {
        if (result.hasErrors()) {
            val errors = result.allErrors.map { (it as FieldError).defaultMessage }.joinToString(", ")
            return ResponseEntity.badRequest().body(mapOf("error" to "Validation failed", "message" to errors))
        }

        val response = authService.login(loginRequest)
        val (user, token) = response
        return ResponseEntity.ok(mapOf("token" to token, "user" to user))
    }

    @PatchMapping(ApiRoutes.AuthRoutes.UPDATE_PASSWORD)
    fun updatePassword(
        @RequestHeader("Authorization") token: String,
        @Valid @RequestBody updatePasswordRequest: UpdatePasswordRequest,
        result: BindingResult
    ): ResponseEntity<Any> {
        if (result.hasErrors()) {
            val errors = result.allErrors.map { (it as FieldError).defaultMessage }.joinToString(", ")
            return ResponseEntity.badRequest().body(mapOf("error" to "Validation failed", "message" to errors))
        }
        authService.updatePassword(token.extractBearerToken(), updatePasswordRequest)
        return ResponseEntity.ok(mapOf("message" to "Password updated successfully"))
    }
}
