package me.sahil.book_management.auth.service

import me.sahil.book_management.auth.dto.LoginRequest
import me.sahil.book_management.auth.dto.RegisterRequest
import me.sahil.book_management.user.entity.User
import me.sahil.book_management.user.repository.UserRepository
import me.sahil.book_management.auth.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import jakarta.transaction.Transactional
import me.sahil.book_management.user.dto.UserResponseDto
import me.sahil.book_management.user.mapper.toUserResponseDto
import org.slf4j.LoggerFactory

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {

    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    @Transactional
    fun register(registerRequest: RegisterRequest): Pair<UserResponseDto, String> {
        // Check if the email already exists
        val existingUser = userRepository.findByEmail(registerRequest.email)
        if (existingUser != null) {
            logger.error("Attempt to register with an existing email: ${registerRequest.email}")
            throw IllegalArgumentException("Email already exists")
        }

        // Encode the password before saving
        val encodedPassword = passwordEncoder.encode(registerRequest.password)

        // Create a new User entity with optional age and image
        val user = User(
            name = registerRequest.name,
            email = registerRequest.email,
            password = encodedPassword,
            role = registerRequest.role,
            age = registerRequest.age,  // Optional field
            image = registerRequest.image // Optional field
        )

        // Save the user to the database
        userRepository.save(user)

        logger.info("User registered successfully with email: ${registerRequest.email}")
        return Pair(user.toUserResponseDto(), "Author registered successfully!")
    }

    @Transactional
    fun login(loginRequest: LoginRequest): Pair<UserResponseDto, String> {
        // Find the user by email
        val user = userRepository.findByEmail(loginRequest.email)
            ?: throw IllegalArgumentException("Invalid email or password")

        // Validate the password
        if (!passwordEncoder.matches(loginRequest.password, user.password)) {
            logger.error("Login failed for email: ${loginRequest.email} - Invalid password")
            throw IllegalArgumentException("Invalid email or password")
        }

        // Generate the JWT token and return it
        val token = jwtTokenProvider.generateToken(user)
        logger.info("User logged in successfully with email: ${loginRequest.email}")
        return Pair(user.toUserResponseDto(), token)
    }
}
