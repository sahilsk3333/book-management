package me.sahil.book_management.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * Data class representing the login request payload for user authentication.
 *
 * This class contains the user's email and password, both of which are required for login.
 * The email must follow a valid email format, and the password cannot be blank.
 * Validation annotations are used to enforce these constraints.
 *
 * @param email The email address of the user, which is required and must be in a valid email format.
 * @param password The password for the user, which is required and cannot be blank.
 */
data class LoginRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)