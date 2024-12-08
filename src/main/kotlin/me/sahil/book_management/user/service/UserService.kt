package me.sahil.book_management.user.service

import me.sahil.book_management.user.dto.PartialUpdateUserRequest
import me.sahil.book_management.user.dto.UpdateUserRequest
import me.sahil.book_management.user.dto.UserResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Interface that defines the user-related business logic for managing users in the system.
 */
interface UserService {

    /**
     * Retrieves all users except the admin, with pagination support.
     *
     * @param token The authorization token of the currently authenticated user.
     * @param pageable The pagination information (page number and size).
     * @return A paginated list of user responses.
     */
    fun getAllUsers(token: String, pageable: Pageable): Page<UserResponse>

    /**
     * Updates the details of an existing user.
     *
     * @param token The authorization token of the currently authenticated user.
     * @param userId The ID of the user to update.
     * @param updateUserRequestDto The DTO containing the updated user details.
     * @return The updated user response.
     * @throws NotFoundException if the user is not found.
     */
    fun updateUser(
        token: String,
        userId: Long,
        updateUserRequestDto: UpdateUserRequest
    ): UserResponse

    /**
     * Partially updates the details of an existing user.
     *
     * @param token The authorization token of the currently authenticated user.
     * @param userId The ID of the user to update.
     * @param partialUpdateUserRequestDto The DTO containing the partial user details to update.
     * @return The updated user response.
     * @throws NotFoundException if the user is not found.
     */
    fun updateUser(
        token: String,
        userId: Long,
        partialUpdateUserRequestDto: PartialUpdateUserRequest
    ): UserResponse

    /**
     * Deletes the user with the given ID.
     *
     * @param token The authorization token of the currently authenticated user.
     * @param userId The ID of the user to delete.
     * @throws NotFoundException if the user is not found.
     * @throws IllegalAccessException if the user is not authorized to delete the target user.
     */
    fun deleteUser(token: String, userId: Long)

    /**
     * Retrieves the details of a user by their ID.
     *
     * @param token The authorization token of the currently authenticated user.
     * @param userId The ID of the user to retrieve.
     * @return The user response.
     * @throws NotFoundException if the user is not found.
     */
    fun getUserById(token: String, userId: Long): UserResponse

    /**
     * Retrieves the details of the user associated with the given token.
     *
     * @param token The authorization token of the currently authenticated user.
     * @return The user response.
     */
    fun getUserByToken(token: String): UserResponse
}
