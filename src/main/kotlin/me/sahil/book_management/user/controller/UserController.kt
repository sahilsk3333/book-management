package me.sahil.book_management.user.controller

import jakarta.validation.Valid
import me.sahil.book_management.user.dto.PartialUpdateUserRequestDto
import me.sahil.book_management.user.dto.UpdateUserRequestDto
import me.sahil.book_management.user.dto.UserResponseDto
import me.sahil.book_management.user.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    // Endpoint to get all users (paginated), accessible by ADMIN only
    @GetMapping
    fun getAllUsers(
        @RequestHeader("Authorization") token: String,
        pageable: Pageable
    ): ResponseEntity<Page<UserResponseDto>> {
        val userList = userService.getAllUsers(token.removePrefix("Bearer "), pageable)
        return ResponseEntity.ok(userList)
    }

    // Endpoint to update user details (allowed only for the user themselves)
    @PutMapping("/{userId}")
    fun updateUser(
        @RequestHeader("Authorization") token: String,
        @PathVariable userId: Long,
        @Valid @RequestBody updateUserRequestDto: UpdateUserRequestDto
    ): ResponseEntity<UserResponseDto> {
        val updatedUser = userService.updateUser(token.removePrefix("Bearer "), userId, updateUserRequestDto)
        return ResponseEntity.ok(updatedUser)
    }

    // Endpoint to partially update user details (allowed only for the user themselves)
    @PatchMapping("/{userId}")
    fun partialUpdateUser(
        @RequestHeader("Authorization") token: String,
        @PathVariable userId: Long,
        @Valid @RequestBody partialUpdateUserRequestDto: PartialUpdateUserRequestDto
    ): ResponseEntity<UserResponseDto> {
        val updatedUser = userService.updateUser(token.removePrefix("Bearer "), userId, partialUpdateUserRequestDto)
        return ResponseEntity.ok(updatedUser)
    }

    // Endpoint to delete a user, accessible by ADMIN only
    @DeleteMapping("/{userId}")
    fun deleteUser(
        @RequestHeader("Authorization") token: String,
        @PathVariable userId: Long
    ): ResponseEntity<String> {
        userService.deleteUser(token.removePrefix("Bearer "), userId)
        return ResponseEntity.ok("User with ID $userId has been deleted successfully.")
    }

    // Endpoint to get a user by ID (accessible to self and admin only)
    @GetMapping("/{userId}")
    fun getUserById(
        @RequestHeader("Authorization") token: String,
        @PathVariable userId: Long
    ): ResponseEntity<UserResponseDto> {
        val user = userService.getUserById(token.removePrefix("Bearer "), userId)
        return ResponseEntity.ok(user)
    }

    // Endpoint to get the user's own details, no need to provide user ID
    @GetMapping("/profile")
    fun getUserByToken(@RequestHeader("Authorization") token: String): ResponseEntity<UserResponseDto> {
        val user = userService.getUserByToken(token.removePrefix("Bearer "))
        return ResponseEntity.ok(user)
    }

}

