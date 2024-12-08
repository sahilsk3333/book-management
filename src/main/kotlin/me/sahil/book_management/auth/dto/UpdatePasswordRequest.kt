package me.sahil.book_management.auth.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdatePasswordRequest(
    @field:NotBlank(message = "Current Password is required")
    val currentPassword: String,

    @field:NotBlank(message = "New Password is required")
    @field:Size(min = 6, message = "Password should be at least 6 characters long")
    val newPassword: String
)