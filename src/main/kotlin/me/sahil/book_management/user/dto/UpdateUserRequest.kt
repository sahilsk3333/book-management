package me.sahil.book_management.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import me.sahil.book_management.core.role.Role

/**
 * Data class representing a request to update a user's details.
 *
 * This class is used to capture the full update request for a user. All fields except for `age` and `image` are required.
 * Validation annotations ensure that the `name`, `email`, and `role` fields are provided with appropriate formats.
 *
 * @property name The name of the user to be updated. This field is required.
 * @property email The email address of the user to be updated. This field is required and must be a valid email format.
 * @property role The role of the user to be updated. This field is required.
 * @property age The age of the user to be updated. This field is optional.
 * @property image The URL or path to the user's profile image to be updated. This field is optional.
 */
data class UpdateUserRequest(
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
