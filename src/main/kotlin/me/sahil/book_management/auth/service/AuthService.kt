package me.sahil.book_management.auth.service

import me.sahil.book_management.auth.dto.LoginRequest
import me.sahil.book_management.auth.dto.RegisterRequest
import me.sahil.book_management.user.entity.User
import me.sahil.book_management.user.repository.UserRepository
import me.sahil.book_management.auth.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import jakarta.transaction.Transactional
import me.sahil.book_management.auth.dto.UpdatePasswordRequest
import me.sahil.book_management.file.repository.FileRepository
import me.sahil.book_management.user.dto.UserResponse
import me.sahil.book_management.user.mapper.toUserResponseDto
import org.slf4j.LoggerFactory

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val fileRepository: FileRepository
) {

    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    @Transactional
    fun register(registerRequest: RegisterRequest): Pair<UserResponse, String> {
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

        // If the image URL is provided, check if it exists in the file table and mark as used
        registerRequest.image?.let { imageUrl ->
            val file = fileRepository.findByDownloadUrl(imageUrl)
            if (file != null) {
                // Mark the file as used
                fileRepository.save(file.copy(
                    isUsed = true
                ))
                logger.info("Image URL $imageUrl marked as used")
            } else {
                logger.warn("No file found with the provided image URL: $imageUrl")
            }
        }

        logger.info("User registered successfully with email: ${registerRequest.email}")
        return Pair(user.toUserResponseDto(), "User registered successfully!")
    }

    @Transactional
    fun login(loginRequest: LoginRequest): Pair<UserResponse, String> {
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

    @Transactional
    fun updatePassword(token: String, updatePasswordRequest: UpdatePasswordRequest) {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Fetch the user from the database
        val user = userRepository.findById(userClaims.id)
            .orElseThrow { IllegalArgumentException("User not found") }

        // Validate the current password
        if (!passwordEncoder.matches(updatePasswordRequest.currentPassword, user.password)) {
            throw IllegalArgumentException("Current password is incorrect")
        }

        // Encode the new password and update the user
        val encodedNewPassword = passwordEncoder.encode(updatePasswordRequest.newPassword)
        userRepository.save(user.copy(password = encodedNewPassword))

        logger.info("Password updated successfully for user with ID: ${user.id}")
    }

}
