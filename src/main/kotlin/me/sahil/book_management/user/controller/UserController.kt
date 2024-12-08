package me.sahil.book_management.user.controller

import jakarta.validation.Valid
import me.sahil.book_management.core.route.ApiRoutes
import me.sahil.book_management.core.utils.extractBearerToken
import me.sahil.book_management.user.dto.PartialUpdateUserRequest
import me.sahil.book_management.user.dto.UpdateUserRequest
import me.sahil.book_management.user.dto.UserResponse
import me.sahil.book_management.user.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for managing user-related operations.
 *
 * This controller provides endpoints for retrieving, updating, deleting, and managing user data.
 * Access to some endpoints is restricted to certain roles (e.g., ADMIN), while others are available to users based on their token.
 */
@RestController
@RequestMapping(ApiRoutes.UserRoutes.PATH)
class UserController(
    private val userService: UserService
) {

    /**
     * Endpoint to get a paginated list of all users.
     *
     * This endpoint is accessible only to users with the ADMIN role.
     * It returns a paginated list of users.
     *
     * @param token The authentication token (Bearer token) provided in the Authorization header.
     * @param pageable The pagination parameters (page size, page number).
     * @return A paginated list of users.
     * @throws UnauthorizedAccessException if the user does not have the ADMIN role.
     */
    @GetMapping(ApiRoutes.UserRoutes.GET_ALL_USERS)
    fun getAllUsers(
        @RequestHeader("Authorization") token: String,
        pageable: Pageable
    ): ResponseEntity<Page<UserResponse>> {
        val userList = userService.getAllUsers(token.removePrefix("Bearer "), pageable)
        return ResponseEntity.ok(userList)
    }

    /**
     * Endpoint to update user details.
     *
     * This endpoint is accessible only by the user themselves. The user can update their own details.
     *
     * @param token The authentication token (Bearer token) provided in the Authorization header.
     * @param userId The ID of the user to be updated.
     * @param updateUserRequestDto The request body containing the updated user details.
     * @return The updated user details.
     * @throws UnauthorizedAccessException if the user tries to update another user's details.
     * @throws UserNotFoundException if the user with the provided ID does not exist.
     */
    @PutMapping(ApiRoutes.UserRoutes.UPDATE_USER)
    fun updateUser(
        @RequestHeader("Authorization") token: String,
        @PathVariable userId: Long,
        @Valid @RequestBody updateUserRequestDto: UpdateUserRequest
    ): ResponseEntity<UserResponse> {
        val updatedUser = userService.updateUser(token.extractBearerToken(), userId, updateUserRequestDto)
        return ResponseEntity.ok(updatedUser)
    }

    /**
     * Endpoint to partially update user details.
     *
     * This endpoint is accessible only by the user themselves. The user can partially update their own details.
     *
     * @param token The authentication token (Bearer token) provided in the Authorization header.
     * @param userId The ID of the user to be updated.
     * @param partialUpdateUserRequestDto The request body containing the partial updated user details.
     * @return The updated user details.
     * @throws UnauthorizedAccessException if the user tries to update another user's details.
     * @throws UserNotFoundException if the user with the provided ID does not exist.
     */
    @PatchMapping(ApiRoutes.UserRoutes.PATCH_USER)
    fun partialUpdateUser(
        @RequestHeader("Authorization") token: String,
        @PathVariable userId: Long,
        @Valid @RequestBody partialUpdateUserRequestDto: PartialUpdateUserRequest
    ): ResponseEntity<UserResponse> {
        val updatedUser = userService.updateUser(token.extractBearerToken(), userId, partialUpdateUserRequestDto)
        return ResponseEntity.ok(updatedUser)
    }

    /**
     * Endpoint to delete a user.
     *
     * This endpoint is accessible only to users with the ADMIN role. It allows an admin to delete a user by ID.
     *
     * @param token The authentication token (Bearer token) provided in the Authorization header.
     * @param userId The ID of the user to be deleted.
     * @return A success message indicating that the user was deleted.
     * @throws UnauthorizedAccessException if the user does not have the ADMIN role.
     * @throws UserNotFoundException if the user with the provided ID does not exist.
     */
    @DeleteMapping(ApiRoutes.UserRoutes.DELETE_USER)
    fun deleteUser(
        @RequestHeader("Authorization") token: String,
        @PathVariable userId: Long
    ): ResponseEntity<String> {
        userService.deleteUser(token.extractBearerToken(), userId)
        return ResponseEntity.ok("User with ID $userId has been deleted successfully.")
    }

    /**
     * Endpoint to get a user by their ID.
     *
     * This endpoint is accessible to both the user themselves and users with the ADMIN role.
     * It returns the details of the user with the provided ID.
     *
     * @param token The authentication token (Bearer token) provided in the Authorization header.
     * @param userId The ID of the user to be retrieved.
     * @return The user details.
     * @throws UnauthorizedAccessException if the user does not have permission to view the requested user's details.
     * @throws UserNotFoundException if the user with the provided ID does not exist.
     */
    @GetMapping(ApiRoutes.UserRoutes.GET_USER_BY_ID)
    fun getUserById(
        @RequestHeader("Authorization") token: String,
        @PathVariable userId: Long
    ): ResponseEntity<UserResponse> {
        val user = userService.getUserById(token.extractBearerToken(), userId)
        return ResponseEntity.ok(user)
    }

    /**
     * Endpoint to get the authenticated user's own details.
     *
     * This endpoint is accessible only to the authenticated user and returns their own user details.
     *
     * @param token The authentication token (Bearer token) provided in the Authorization header.
     * @return The authenticated user's details.
     * @throws UnauthorizedAccessException if the user is not authenticated.
     */
    @GetMapping(ApiRoutes.UserRoutes.PROFILE)
    fun getUserByToken(@RequestHeader("Authorization") token: String): ResponseEntity<UserResponse> {
        val user = userService.getUserByToken(token.extractBearerToken())
        return ResponseEntity.ok(user)
    }
}


