package me.sahil.book_management.auth.controller

import me.sahil.book_management.auth.dto.UserResponseDto
import me.sahil.book_management.auth.mapper.toUserResponseDto
import me.sahil.book_management.auth.security.JwtTokenProvider
import me.sahil.book_management.auth.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider
) {

    // Endpoint to get all users, accessible by ADMIN only
    @GetMapping
    fun getAllUsers(@RequestHeader("Authorization") token: String): ResponseEntity<List<UserResponseDto>> {
        // Extract the token without the "Bearer " prefix
        val userList = userService.getAllUsers(token.removePrefix("Bearer "))
        return ResponseEntity.ok(userList.map { it.toUserResponseDto() })
    }

    // Endpoint to delete a user, accessible by ADMIN only
    @DeleteMapping("/{userId}")
    fun deleteUser(@RequestHeader("Authorization") token: String, @PathVariable userId: Long): ResponseEntity<String> {
        userService.deleteUser(token.removePrefix("Bearer "), userId)
        return ResponseEntity.ok("User with ID $userId has been deleted successfully.")
    }
}
