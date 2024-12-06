package me.sahil.book_management.auth.dto

import me.sahil.book_management.common.role.Role

data class UserResponseDto(
    val id: Long,
    val name: String,
    val email: String,
    val age: Int?,
    val image: String?,
    val role: Role
)
