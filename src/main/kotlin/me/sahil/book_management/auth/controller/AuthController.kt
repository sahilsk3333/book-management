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
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

/**
 * Controller for handling authentication-related requests such as registration, login, and password update.
 *
 * This class provides endpoints for user registration, login, and updating passwords. It uses the [AuthService]
 * to handle the core logic behind these actions.
 */
@RestController
@RequestMapping(ApiRoutes.AuthRoutes.PATH)
class AuthController(private val authService: AuthService) {

    /**
     * Endpoint for user registration.
     *
     * This endpoint accepts a POST request to register a new user. It validates the request body and uses
     * the [AuthService] to perform the registration logic. Upon successful registration, it returns a
     * response with a 201 status, containing the newly created user and a success message.
     *
     * @param registerRequest The request body containing the user's registration data.
     * @param result BindingResult to hold validation errors, if any.
     * @return A [ResponseEntity] with status 201 and a body containing a message and the newly registered user.
     */
    @PostMapping(ApiRoutes.AuthRoutes.REGISTER)
    fun register(@Valid @RequestBody registerRequest: RegisterRequest, result: BindingResult): ResponseEntity<Any> {
        if (result.hasErrors()) {
            throw BindException(result)
        }
        val response = authService.register(registerRequest)
        val (user, message) = response
        return ResponseEntity.status(HttpStatus.CREATED).body(mapOf("message" to message, "user" to user))
    }

    /**
     * Endpoint for user login.
     *
     * This endpoint accepts a POST request to log in a user. It validates the login request body and calls
     * the [AuthService] to handle the login process. If the login is successful, it returns a response
     * with a 200 status, containing the user's information and an authentication token.
     *
     * @param loginRequest The request body containing the user's login credentials.
     * @param result BindingResult to hold validation errors, if any.
     * @return A [ResponseEntity] with status 200 and a body containing the authentication token and user information.
     */
    @PostMapping(ApiRoutes.AuthRoutes.LOGIN)
    fun login(@Valid @RequestBody loginRequest: LoginRequest, result: BindingResult): ResponseEntity<Any> {
        if (result.hasErrors()) {
            throw BindException(result)
        }
        val response = authService.login(loginRequest)
        val (user, token) = response
        return ResponseEntity.ok(mapOf("token" to token, "user" to user))
    }

    /**
     * Endpoint for updating a user's password.
     *
     * This endpoint accepts a PATCH request to update the user's password. The request must include a valid
     * authentication token in the [Authorization] header. The password update request is validated and
     * passed to the [AuthService] to update the password. Upon successful update, it returns a response with
     * a success message.
     *
     * @param token The authentication token passed in the request header.
     * @param updatePasswordRequest The request body containing the new password data.
     * @param result BindingResult to hold validation errors, if any.
     * @return A [ResponseEntity] with status 200 and a success message upon successful password update.
     */
    @PatchMapping(ApiRoutes.AuthRoutes.UPDATE_PASSWORD)
    fun updatePassword(
        @RequestHeader("Authorization") token: String,
        @Valid @RequestBody updatePasswordRequest: UpdatePasswordRequest,
        result: BindingResult
    ): ResponseEntity<Any> {
        if (result.hasErrors()) {
            throw BindException(result)
        }
        authService.updatePassword(token.extractBearerToken(), updatePasswordRequest)
        return ResponseEntity.ok(mapOf("message" to "Password updated successfully"))
    }
}
