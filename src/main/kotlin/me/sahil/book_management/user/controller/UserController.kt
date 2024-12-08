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

@RestController
@RequestMapping(ApiRoutes.UserRoutes.PATH)
class UserController(
    private val userService: UserService
) {

    // Endpoint to get all users (paginated), accessible by ADMIN only
    @GetMapping(ApiRoutes.UserRoutes.GET_ALL_USERS)
    fun getAllUsers(
        @RequestHeader("Authorization") token: String,
        pageable: Pageable
    ): ResponseEntity<Page<UserResponse>> {
        val userList = userService.getAllUsers(token.removePrefix("Bearer "), pageable)
        return ResponseEntity.ok(userList)
    }

    // Endpoint to update user details (allowed only for the user themselves)
    @PutMapping(ApiRoutes.UserRoutes.UPDATE_USER)
    fun updateUser(
        @RequestHeader("Authorization") token: String,
        @PathVariable userId: Long,
        @Valid @RequestBody updateUserRequestDto: UpdateUserRequest
    ): ResponseEntity<UserResponse> {
        val updatedUser = userService.updateUser(token.extractBearerToken(), userId, updateUserRequestDto)
        return ResponseEntity.ok(updatedUser)
    }

    // Endpoint to partially update user details (allowed only for the user themselves)
    @PatchMapping(ApiRoutes.UserRoutes.PATCH_USER)
    fun partialUpdateUser(
        @RequestHeader("Authorization") token: String,
        @PathVariable userId: Long,
        @Valid @RequestBody partialUpdateUserRequestDto: PartialUpdateUserRequest
    ): ResponseEntity<UserResponse> {
        val updatedUser = userService.updateUser(token.extractBearerToken(), userId, partialUpdateUserRequestDto)
        return ResponseEntity.ok(updatedUser)
    }

    // Endpoint to delete a user, accessible by ADMIN only
    @DeleteMapping(ApiRoutes.UserRoutes.DELETE_USER)
    fun deleteUser(
        @RequestHeader("Authorization") token: String,
        @PathVariable userId: Long
    ): ResponseEntity<String> {
        userService.deleteUser(token.extractBearerToken(), userId)
        return ResponseEntity.ok("User with ID $userId has been deleted successfully.")
    }

    // Endpoint to get a user by ID (accessible to self and admin only)
    @GetMapping(ApiRoutes.UserRoutes.GET_USER_BY_ID)
    fun getUserById(
        @RequestHeader("Authorization") token: String,
        @PathVariable userId: Long
    ): ResponseEntity<UserResponse> {
        val user = userService.getUserById(token.extractBearerToken(), userId)
        return ResponseEntity.ok(user)
    }

    // Endpoint to get the user's own details,
    @GetMapping(ApiRoutes.UserRoutes.PROFILE)
    fun getUserByToken(@RequestHeader("Authorization") token: String): ResponseEntity<UserResponse> {
        val user = userService.getUserByToken(token.extractBearerToken())
        return ResponseEntity.ok(user)
    }

}

