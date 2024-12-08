package me.sahil.book_management.user.service

import me.sahil.book_management.user.dto.PartialUpdateUserRequest
import me.sahil.book_management.user.dto.UpdateUserRequest
import me.sahil.book_management.user.dto.UserResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserService {

    fun getAllUsers(token: String, pageable: Pageable): Page<UserResponse>

    fun updateUser(token: String, userId: Long, updateUserRequestDto: UpdateUserRequest): UserResponse

    fun updateUser(
        token: String,
        userId: Long,
        partialUpdateUserRequestDto: PartialUpdateUserRequest
    ): UserResponse

    fun deleteUser(token: String, userId: Long)

    fun getUserById(token: String, userId: Long): UserResponse

    fun getUserByToken(token: String): UserResponse
}