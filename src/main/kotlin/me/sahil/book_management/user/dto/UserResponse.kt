package me.sahil.book_management.user.dto

import me.sahil.book_management.core.role.Role
/**
 * Data class representing the response for user details.
 *
 * This class is used to return a user's details in response to a request, typically after fetching the user from
 * the database or updating their information. It contains essential user information such as the user's `id`, `name`,
 * `email`, `age`, `image`, and `role`.
 *
 * @property id The unique identifier of the user.
 * @property name The name of the user.
 * @property email The email address of the user.
 * @property age The age of the user. This field is nullable as the age may not always be available.
 * @property image The URL or path to the user's profile image. This field is nullable as the user may not have an image.
 * @property role The role of the user (e.g., admin, user, etc.).
 */
data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val age: Int?,
    val image: String?,
    val role: Role
)
