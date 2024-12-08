package me.sahil.book_management.user.mapper

import me.sahil.book_management.user.dto.UserResponse
import me.sahil.book_management.user.entity.User

fun User.toUserResponseDto() = UserResponse(
    id = this.id,
    name = this.name,
    image = this.image,
    age = this.age,
    email = this.email,
    role = this.role
)