package me.sahil.book_management.auth.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * Data class representing the request payload for updating a user's password.
 *
 * This class contains the necessary information for updating a user's password,
 * including the current password and the new password. The class uses validation annotations
 * to ensure that both passwords are provided and that the new password meets the minimum length requirement.
 *
 * @param currentPassword The current password of the user, which is required and cannot be blank.
 * @param newPassword The new password for the user, which is required and must be at least 6 characters long.
 */
data class UpdatePasswordRequest(
    @field:NotBlank(message = "Current Password is required")
    val currentPassword: String,

    @field:NotBlank(message = "New Password is required")
    @field:Size(min = 6, message = "Password should be at least 6 characters long")
    val newPassword: String
)