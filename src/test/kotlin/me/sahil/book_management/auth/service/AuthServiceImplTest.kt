package me.sahil.book_management.auth.service

import me.sahil.book_management.auth.dto.LoginRequest
import me.sahil.book_management.auth.dto.RegisterRequest
import me.sahil.book_management.auth.dto.UpdatePasswordRequest
import me.sahil.book_management.auth.security.JwtTokenProvider
import me.sahil.book_management.core.role.Role
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
import org.mockito.Mockito.mock

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
class AuthServiceImplTest {

    private val userRepository: UserRepository = mock()
    private val passwordEncoder: PasswordEncoder = mock()
    private val jwtTokenProvider: JwtTokenProvider = mock()
    private val fileRepository: FileRepository = mock()

    private val authService = AuthServiceImpl(userRepository, passwordEncoder, jwtTokenProvider, fileRepository)

    // Test case for successful user registration
    @Test
    fun `should register user successfully`() {
        val registerRequest = RegisterRequest(
            name = "John Doe",
            email = "john.doe@example.com",
            password = "password123",
            role = Role.READER,
            age = 30,
            image = null
        )

        val userResponse = UserResponse(
            id = 1L,
            name = "John Doe",
            email = "john.doe@example.com",
            age = 30,
            image = null,
            role = Role.READER
        )

        val user = User(
            id = 1L,
            name = "John Doe",
            email = "john.doe@example.com",
            password = "encodedPassword", // mocked
            role = Role.READER,
            age = 30,
            image = null
        )

        // Mock the repository and password encoder
        Mockito.`when`(userRepository.findByEmail(registerRequest.email)).thenReturn(null)
        Mockito.`when`(passwordEncoder.encode(registerRequest.password)).thenReturn("encodedPassword")
        Mockito.`when`(userRepository.save(any())).thenReturn(user)

        // Call the register method
        val result = authService.register(registerRequest)

        assertNotNull(result)
        assertEquals("User registered successfully!", result.second)
        assertEquals("John Doe", result.first.name)
        assertEquals("john.doe@example.com", result.first.email)
    }

    // Test case for registration failure when email already exists
    @Test
    fun `should throw exception when email already exists`() {
        val registerRequest = RegisterRequest(
            name = "John Doe",
            email = "john.doe@example.com",
            password = "password123",
            role = Role.READER,
            age = 30,
            image = null
        )

        val existingUser = User(
            id = 1L,
            name = "John Doe",
            email = "john.doe@example.com",
            password = "encodedPassword",
            role = Role.READER,
            age = 30,
            image = null
        )

        // Mock the repository
        Mockito.`when`(userRepository.findByEmail(registerRequest.email)).thenReturn(existingUser)

        // Call the register method and expect an exception
        val exception = assertThrows<IllegalArgumentException> {
            authService.register(registerRequest)
        }

        assertEquals("Email already exists", exception.message)
    }

    // Test case for successful user login
    @Test
    fun `should login user successfully`() {
        val loginRequest = LoginRequest(
            email = "john.doe@example.com",
            password = "password123"
        )

        val user = User(
            id = 1L,
            name = "John Doe",
            email = "john.doe@example.com",
            password = "encodedPassword",  // encoded password
            role = Role.READER,
            age = 30,
            image = null
        )

        // Mock the repository and password encoder
        Mockito.`when`(userRepository.findByEmail(loginRequest.email)).thenReturn(user)
        Mockito.`when`(passwordEncoder.matches(loginRequest.password, user.password)).thenReturn(true)
        Mockito.`when`(jwtTokenProvider.generateToken(user)).thenReturn("fake-jwt-token")

        // Call the login method
        val result = authService.login(loginRequest)

        assertNotNull(result)
        assertEquals("fake-jwt-token", result.second)
        assertEquals("john.doe@example.com", result.first.email)
    }

    // Test case for login failure when email or password is incorrect
    @Test
    fun `should throw exception when email or password is incorrect`() {
        val loginRequest = LoginRequest(
            email = "john.doe@example.com",
            password = "password123"
        )

        // Mock the repository to return null for invalid email
        Mockito.`when`(userRepository.findByEmail(loginRequest.email)).thenReturn(null)

        // Call the login method and expect an exception
        val exception = assertThrows<IllegalArgumentException> {
            authService.login(loginRequest)
        }

        assertEquals("Invalid email or password", exception.message)
    }

    // Test case for successful password update
    @Test
    fun `should update password successfully`() {
        val token = "valid-jwt-token"
        val updatePasswordRequest = UpdatePasswordRequest(
            currentPassword = "currentPassword123",
            newPassword = "newPassword456"
        )

        val user = User(
            id = 1L,
            name = "John Doe",
            email = "john.doe@example.com",
            password = "encodedCurrentPassword", // encoded current password
            role = Role.READER,
            age = 30,
            image = null
        )

        // Mock the repository, password encoder, and jwtTokenProvider
        Mockito.`when`(jwtTokenProvider.getUserDetailsFromToken(token)).thenReturn(
            JwtTokenProvider.UserClaims(
                user.id,
                user.email,
                user.name,
                user.role
            )
        )
        Mockito.`when`(userRepository.findById(user.id)).thenReturn(Optional.of(user))
        Mockito.`when`(passwordEncoder.matches(updatePasswordRequest.currentPassword, user.password)).thenReturn(true)
        Mockito.`when`(passwordEncoder.encode(updatePasswordRequest.newPassword)).thenReturn("encodedNewPassword")

        // Call the update password method
        authService.updatePassword(token, updatePasswordRequest)

        // Verify the update
        Mockito.verify(userRepository).save(Mockito.argThat { it.password == "encodedNewPassword" })
    }

    // Test case for failed password update due to incorrect current password
    @Test
    fun `should throw exception when current password is incorrect`() {
        val token = "valid-jwt-token"
        val updatePasswordRequest = UpdatePasswordRequest(
            currentPassword = "wrongCurrentPassword",
            newPassword = "newPassword456"
        )

        val user = User(
            id = 1L,
            name = "John Doe",
            email = "john.doe@example.com",
            password = "encodedCurrentPassword",
            role = Role.READER,
            age = 30,
            image = null
        )

        // Mock the repository and password encoder
        Mockito.`when`(jwtTokenProvider.getUserDetailsFromToken(token)).thenReturn(
            JwtTokenProvider.UserClaims(
                user.id,
                user.email,
                user.name,
                user.role
            )
        )
        Mockito.`when`(userRepository.findById(user.id)).thenReturn(Optional.of(user))
        Mockito.`when`(passwordEncoder.matches(updatePasswordRequest.currentPassword, user.password)).thenReturn(false)

        // Call the update password method and expect an exception
        val exception = assertThrows<IllegalArgumentException> {
            authService.updatePassword(token, updatePasswordRequest)
        }

        assertEquals("Current password is incorrect", exception.message)
    }
}
