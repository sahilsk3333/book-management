package me.sahil.book_management.user.dto

import me.sahil.book_management.common.role.Role


data class PartialUpdateUserRequest(

    val name: String?,

    val email: String?,

    val role: Role?,

    val age: Int?,

    val image: String?
)
