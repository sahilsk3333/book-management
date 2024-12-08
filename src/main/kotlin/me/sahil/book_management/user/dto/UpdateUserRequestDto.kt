package me.sahil.book_management.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import me.sahil.book_management.common.role.Role


data class UpdateUserRequestDto(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field: NotNull(message = "Role is required")
    val role: Role,

    val age: Int?,

    val image: String?
)
