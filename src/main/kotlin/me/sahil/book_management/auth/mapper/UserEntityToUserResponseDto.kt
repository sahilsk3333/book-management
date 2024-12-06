package me.sahil.book_management.auth.mapper

import me.sahil.book_management.auth.dto.UserResponseDto
import me.sahil.book_management.auth.entity.User

fun User.toUserResponseDto() = UserResponseDto(
    id = this.id,
    name = this.name,
    image = this.image,
    age = this.age,
    email = this.email,
    role = this.role
)