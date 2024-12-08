package me.sahil.book_management.auth.service

import me.sahil.book_management.auth.dto.LoginRequest
import me.sahil.book_management.auth.dto.RegisterRequest
import me.sahil.book_management.auth.dto.UpdatePasswordRequest
import me.sahil.book_management.user.dto.UserResponse

interface AuthService {
    fun register(registerRequest: RegisterRequest): Pair<UserResponse, String>
    fun login(loginRequest: LoginRequest): Pair<UserResponse, String>

    fun updatePassword(token: String, updatePasswordRequest: UpdatePasswordRequest)
}