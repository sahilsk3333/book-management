package me.sahil.book_management.auth.service

import me.sahil.book_management.auth.dto.LoginRequest
import me.sahil.book_management.auth.dto.RegisterRequest
import me.sahil.book_management.auth.dto.UpdatePasswordRequest
import me.sahil.book_management.user.dto.UserResponse

/**
 * Service interface for handling authentication-related operations.
 *
 * This interface defines methods for user registration, login, and password update.
 * The service handles the business logic associated with these operations, including
 * user creation, token generation, and password management.
 */
interface AuthService {

    /**
     * Registers a new user with the provided registration details.
     *
     * This method processes the registration request, creates a new user, and returns a
     * response containing the user details and a success message.
     *
     * @param registerRequest The registration request containing the user's details, including
     *                        name, email, password, role, age, and image.
     * @return A pair containing the created `UserResponse` and a success message.
     */
    fun register(registerRequest: RegisterRequest): Pair<UserResponse, String>

    /**
     * Logs in a user with the provided credentials.
     *
     * This method validates the login credentials, generates a JWT token for the user,
     * and returns a response containing the user details and the generated token.
     *
     * @param loginRequest The login request containing the user's email and password.
     * @return A pair containing the `UserResponse` and the generated JWT token.
     */
    fun login(loginRequest: LoginRequest): Pair<UserResponse, String>

    /**
     * Updates the user's password.
     *
     * This method allows a user to update their password by providing the current password
     * and a new password. The request requires a valid JWT token for authentication.
     *
     * @param token The JWT token used for user authentication.
     * @param updatePasswordRequest The request containing the current and new password.
     * @throws TokenMissingException If the provided token is invalid or missing.
     */
    fun updatePassword(token: String, updatePasswordRequest: UpdatePasswordRequest)
}
