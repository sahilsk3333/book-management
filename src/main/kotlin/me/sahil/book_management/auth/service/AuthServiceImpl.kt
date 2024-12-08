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

/**
 * Implementation of the [AuthService] interface that handles user authentication operations.
 *
 * This service includes logic for registering a new user, logging in an existing user,
 * and updating the user's password. The service interacts with the database to store and
 * retrieve user data, applies password encoding for security, and generates JWT tokens
 * for authenticated users.
 *
 * @param userRepository The repository for interacting with user data in the database.
 * @param passwordEncoder The password encoder used to encode passwords before saving.
 * @param jwtTokenProvider The utility class for generating and validating JWT tokens.
 * @param fileRepository The repository for interacting with file data in the database.
 */
@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val fileRepository: FileRepository
) : AuthService {

    private val logger = LoggerFactory.getLogger(AuthServiceImpl::class.java)

    /**
     * Registers a new user with the provided registration details.
     *
     * This method checks if the email already exists in the database, encodes the user's
     * password, creates a new user entity, and saves it in the repository. If an image URL
     * is provided, it checks if the file exists in the file repository and marks it as used.
     *
     * @param registerRequest The registration request containing the user's details.
     * @return A pair containing the created `UserResponse` and a success message.
     * @throws IllegalArgumentException If the email is already in use.
     */
    @Transactional
    override fun register(registerRequest: RegisterRequest): Pair<UserResponse, String> {
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
                fileRepository.save(
                    file.copy(
                        isUsed = true
                    )
                )
                logger.info("Image URL $imageUrl marked as used")
            } else {
                logger.warn("No file found with the provided image URL: $imageUrl")
            }
        }

        logger.info("User registered successfully with email: ${registerRequest.email}")
        return Pair(user.toUserResponseDto(), "User registered successfully!")
    }

    /**
     * Logs in a user with the provided credentials.
     *
     * This method validates the user's email and password, generates a JWT token if valid,
     * and returns the user's details along with the token.
     *
     * @param loginRequest The login request containing the user's email and password.
     * @return A pair containing the `UserResponse` and the generated JWT token.
     * @throws IllegalArgumentException If the email or password is incorrect.
     */
    @Transactional
    override fun login(loginRequest: LoginRequest): Pair<UserResponse, String> {
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

    /**
     * Updates the user's password.
     *
     * This method verifies the current password and encodes and saves the new password.
     *
     * @param token The JWT token used to authenticate the user.
     * @param updatePasswordRequest The request containing the current and new password.
     * @throws IllegalArgumentException If the current password is incorrect or the user is not found.
     */
    @Transactional
    override fun updatePassword(token: String, updatePasswordRequest: UpdatePasswordRequest) {
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
