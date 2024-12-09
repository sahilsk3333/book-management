package me.sahil.book_management.auth.service

import jakarta.validation.ConstraintViolationException
import me.sahil.book_management.auth.dto.LoginRequest
import me.sahil.book_management.auth.dto.RegisterRequest
import me.sahil.book_management.auth.dto.UpdatePasswordRequest
import me.sahil.book_management.auth.security.JwtTokenProvider
import me.sahil.book_management.core.role.Role
import me.sahil.book_management.file.Entity.File
import me.sahil.book_management.file.repository.FileRepository
import me.sahil.book_management.user.dto.UserResponse
import me.sahil.book_management.user.entity.User
import me.sahil.book_management.user.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import java.util.*

@SpringBootTest
@Transactional
class AuthServiceImplTest @Autowired constructor(
    private val authService: AuthServiceImpl,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    // Test for successful user registration
    @Test
    fun `should register user successfully`() {
        val registerRequest = RegisterRequest(
            name = "John Doe",
            email = "john.doe@example.com",
            password = "password123",
            role = Role.READER,
            age = 30,
        )

        val result = authService.register(registerRequest)

        assertNotNull(result)
        assertEquals("User registered successfully!", result.second)

        val savedUser = userRepository.findByEmail(registerRequest.email)
        assertNotNull(savedUser)
        assertEquals("John Doe", savedUser?.name)
        assertEquals("john.doe@example.com", savedUser?.email)
    }

    // Test case for existing email during registration
    @Transactional
    @Test
    fun `should throw exception when email already exists`() {
        val registerRequest = RegisterRequest(
            name = "John Doe",
            email = "john.doe@example.com",
            password = "password123",
            role = Role.READER,
            age = 30,
        )

        authService.register(registerRequest)

        val exception = assertThrows<IllegalArgumentException> {
            authService.register(registerRequest)
        }

        assertEquals("Email already exists", exception.message)
    }


    // Test for successful login
    @Test
    fun `should login successfully with valid credentials`() {
        authService.register(
            RegisterRequest(
                name = "John Doe",
                email = "john.doe@example.com",
                password = "password123",
                role = Role.READER,
                age = 30,
            )
        )

        val loginRequest = LoginRequest(
            email = "john.doe@example.com",
            password = "password123"
        )

        val result = authService.login(loginRequest)

        assertNotNull(result)
        assertNotNull(result.second) // Check that token is returned
    }

    // Test case for invalid login with wrong email
    @Test
    fun `should throw exception when login with invalid email`() {

        authService.register(
            RegisterRequest(
                name = "John Doe",
                email = "john.doe@example.com",
                password = "password123",
                role = Role.READER,
                age = 30,
            )
        )

        val loginRequest = LoginRequest(
            email = "nonexistent@example.com",
            password = "password123"
        )

        val exception = assertThrows<IllegalArgumentException> {
            authService.login(loginRequest)
        }

        assertEquals("Invalid email or password", exception.message)
    }

    // Test case for invalid login with wrong password
    @Test
    fun `should throw exception when login with invalid password`() {
        authService.register(
            RegisterRequest(
                name = "John Doe",
                email = "john.doe@example.com",
                password = "password123",
                role = Role.READER,
                age = 30,
            )
        )

        val loginRequest = LoginRequest(
            email = "john.doe@example.com",
            password = "wrongPassword"
        )

        val exception = assertThrows<IllegalArgumentException> {
            authService.login(loginRequest)
        }

        assertEquals("Invalid email or password", exception.message)
    }

    // Test for updating password successfully
    @Test
    fun `should update password successfully`() {
        authService.register(
            RegisterRequest(
                name = "John Doe",
                email = "john.doe@example.com",
                password = "password123",
                role = Role.READER,
                age = 30,
            )
        )


        val (user,token) = authService.login(
            LoginRequest(
                email = "john.doe@example.com",
                password = "password123",
            )
        )

        val updatePasswordRequest = UpdatePasswordRequest(
            currentPassword = "password123",
            newPassword = "newPassword123"
        )

        authService.updatePassword(token, updatePasswordRequest)

        val updatedUser = userRepository.findById(user.id).get()
        assertTrue(passwordEncoder.matches("newPassword123", updatedUser.password))
    }

    // Test case for incorrect current password during password update
    @Test
    fun `should throw exception when updating password with incorrect current password`() {
        authService.register(
            RegisterRequest(
                name = "John Doe",
                email = "john.doe@example.com",
                password = "password123",
                role = Role.READER,
                age = 30,
            )
        )


        val (user,token) = authService.login(
            LoginRequest(
                email = "john.doe@example.com",
                password = "password123",
            )
        )

        val updatePasswordRequest = UpdatePasswordRequest(
            currentPassword = "incorrectPassword",
            newPassword = "newPassword123"
        )

        val exception = assertThrows<IllegalArgumentException> {
            authService.updatePassword(token, updatePasswordRequest)
        }

        assertEquals("Current password is incorrect", exception.message)
    }

    // Test for missing fields in registration (e.g., email)
    @Test
    fun `should throw exception when registering with missing fields`() {
        val registerRequest = RegisterRequest(
            name = "John Doe",
            email = "",
            password = "password123",
            role = Role.READER,
            age = 30,
        )

        val exception = assertThrows<ConstraintViolationException> {
            authService.register(registerRequest)
        }

        assertEquals("Email is required", exception.constraintViolations.first().message)
    }
}
