package me.sahil.book_management.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import me.sahil.book_management.core.role.Role

/**
 * Data class representing the registration request payload for user account creation.
 *
 * This class contains the necessary information for registering a new user, including
 * the user's name, email, password, role, age, and an optional image.
 * The class uses validation annotations to ensure that all required fields are provided
 * and that the email, password, and role meet the necessary constraints.
 *
 * @param name The name of the user, which is required and cannot be blank.
 * @param email The email address of the user, which is required and must be in a valid email format.
 * @param password The password for the user, which is required and must be at least 6 characters long.
 * @param role The role of the user, which is required and cannot be null. It must be one of the roles defined in the `Role` enum.
 * @param age The age of the user, which is optional and can be `null`.
 * @param image The URL of the user's profile image, which is optional and can be `null`.
 */
data class RegisterRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, message = "Password should be at least 6 characters long")
    val password: String,

    @field: NotNull(message = "Role is required")
    val role: Role,

    val age: Int?,

    val image: String?
)
