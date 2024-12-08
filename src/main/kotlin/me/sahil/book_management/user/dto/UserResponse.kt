package me.sahil.book_management.user.dto

import me.sahil.book_management.common.role.Role

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val age: Int?,
    val image: String?,
    val role: Role
)
