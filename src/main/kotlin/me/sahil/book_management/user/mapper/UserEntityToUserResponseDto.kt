package me.sahil.book_management.user.mapper

import me.sahil.book_management.user.dto.UserResponse
import me.sahil.book_management.user.entity.User

/**
 * Extension function to convert a [User] entity to a [UserResponse] DTO.
 *
 * This function takes a [User] entity and maps its properties to a [UserResponse] data transfer object
 * (DTO) for returning user details in API responses.
 *
 * @return A [UserResponse] DTO containing the user's details, including their ID, name, email, age, image, and role.
 */
fun User.toUserResponseDto() = UserResponse(
    id = this.id,
    name = this.name,
    image = this.image,
    age = this.age,
    email = this.email,
    role = this.role
)