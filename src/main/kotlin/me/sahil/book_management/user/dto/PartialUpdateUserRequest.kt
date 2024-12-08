package me.sahil.book_management.user.dto

import me.sahil.book_management.core.role.Role

/**
 * Data class representing a request to partially update a user's details.
 *
 * This class contains the fields that can be optionally updated for a user. Each field is nullable,
 * allowing users to update only the specific fields they want to modify.
 *
 * @property name The name of the user to be updated. It can be null if not changing.
 * @property email The email address of the user to be updated. It can be null if not changing.
 * @property role The role of the user to be updated. It can be null if not changing.
 * @property age The age of the user to be updated. It can be null if not changing.
 * @property image The URL or path to the user's profile image to be updated. It can be null if not changing.
 */
data class PartialUpdateUserRequest(

    val name: String?,

    val email: String?,

    val role: Role?,

    val age: Int?,

    val image: String?
)
