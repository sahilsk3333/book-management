package me.sahil.book_management.user.dto

import jakarta.validation.constraints.Size
import me.sahil.book_management.common.role.Role


data class PartialUpdateUserRequestDto(

    val name: String?,

    val email: String?,

    val role: Role?,

    val age: Int?,

    val image: String?
)
