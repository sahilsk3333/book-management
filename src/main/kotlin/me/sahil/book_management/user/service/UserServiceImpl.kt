package me.sahil.book_management.user.service

import me.sahil.book_management.auth.security.JwtTokenProvider
import me.sahil.book_management.core.exception.NotFoundException
import me.sahil.book_management.core.role.Role
import me.sahil.book_management.file.repository.FileRepository
import me.sahil.book_management.user.dto.PartialUpdateUserRequest
import me.sahil.book_management.user.dto.UpdateUserRequest
import me.sahil.book_management.user.dto.UserResponse
import me.sahil.book_management.user.mapper.toUserResponseDto
import me.sahil.book_management.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
/**
 * Implementation of the [UserService] interface, providing business logic for managing users.
 */
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val fileRepository: FileRepository
) : UserService {

    private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    /**
     * Retrieves all users (excluding the admin), with pagination.
     *
     * @param token The authorization token of the currently authenticated user.
     * @param pageable The pagination information for listing users.
     * @return A paginated list of user responses.
     * @throws IllegalAccessException If the user is not an ADMIN.
     */
    @Transactional
    override fun getAllUsers(token: String, pageable: Pageable): Page<UserResponse> {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Ensure the user is an ADMIN
        if (userClaims.role != Role.ADMIN) {
            throw IllegalAccessException("Access Denied. Only ADMIN can view the users list.")
        }

        // Fetch users excluding the admin itself
        return userRepository
            .findAllExcludingAdmin(userClaims.id, pageable)
            .map { user -> user.toUserResponseDto() }
    }

    /**
     * Updates the details of an existing user. Only the user can update their own profile.
     *
     * @param token The authorization token of the currently authenticated user.
     * @param updateUserRequestDto The DTO containing the updated user details.
     * @return The updated user response.
     * @throws IllegalAccessException If the user is trying to update someone else's profile.
     * @throws NotFoundException If the user to update is not found.
     * @throws IllegalArgumentException If the email is already in use by another user.
     */
    @Transactional
    override fun updateUser(
        token: String,
        updateUserRequestDto: UpdateUserRequest
    ): UserResponse {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)


        val user = userRepository.findById(userClaims.id)
            .orElseThrow { NotFoundException("User not found") }

        // Check if the new email is already taken
        if (updateUserRequestDto.email != user.email && userRepository.existsByEmail(updateUserRequestDto.email)) {
            throw IllegalArgumentException("Email already exists.")
        }

        // Apply updates to the user
        val updatedUser = user.copy(
            name = updateUserRequestDto.name,
            email = updateUserRequestDto.email,
            age = updateUserRequestDto.age,
            image = updateUserRequestDto.image,
            role = updateUserRequestDto.role
        )

        // If an image URL is provided, check if it exists and mark it as used
        updatedUser.image?.let { imageUrl ->
            val file = fileRepository.findByDownloadUrl(imageUrl)
            file?.let {
                fileRepository.save(file.copy(isUsed = true))
                logger.info("Image URL $imageUrl marked as used")
            } ?: logger.warn("No file found with the provided image URL: $imageUrl")
        }

        return userRepository.save(updatedUser).toUserResponseDto()
    }

    /**
     * Partially updates the details of an existing user. Only the user can update their own profile.
     *
     * @param token The authorization token of the currently authenticated user.
     * @param partialUpdateUserRequestDto The DTO containing the partial user details.
     * @return The updated user response.
     * @throws IllegalAccessException If the user is trying to update someone else's profile.
     * @throws NotFoundException If the user to update is not found.
     * @throws IllegalArgumentException If the email is already in use by another user.
     */
    @Transactional
    override fun updateUser(
        token: String,
        partialUpdateUserRequestDto: PartialUpdateUserRequest
    ): UserResponse {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        val user = userRepository.findById(userClaims.id)
            .orElseThrow { NotFoundException("User not found") }

        // Check if the new email is already taken
        partialUpdateUserRequestDto.email?.let { newEmail ->
            if (newEmail != user.email && userRepository.existsByEmail(newEmail)) {
                throw IllegalArgumentException("Email already exists.")
            }
        }

        // Apply partial updates to the user
        val updatedUser = user.copy(
            name = partialUpdateUserRequestDto.name ?: user.name,
            email = partialUpdateUserRequestDto.email ?: user.email,
            age = partialUpdateUserRequestDto.age ?: user.age,
            image = partialUpdateUserRequestDto.image ?: user.image,
            role = partialUpdateUserRequestDto.role ?: user.role
        )

        // Handle image URL if provided
        partialUpdateUserRequestDto.image?.let { imageUrlForUpdate ->
            if (imageUrlForUpdate != user.image) {
                val file = fileRepository.findByDownloadUrl(imageUrlForUpdate)
                file?.let {
                    fileRepository.save(file.copy(isUsed = true))
                    logger.info("Image URL $imageUrlForUpdate marked as used")
                } ?: logger.warn("No file found with the provided image URL: $imageUrlForUpdate")
            }
        }

        return userRepository.save(updatedUser).toUserResponseDto()
    }

    /**
     * Deletes a user. Only an ADMIN can delete users, and users with roles AUTHOR or READER can be deleted.
     *
     * @param token The authorization token of the currently authenticated user.
     * @param userId The ID of the user to delete.
     * @throws IllegalAccessException If the user is not an ADMIN or if trying to delete an ADMIN user.
     * @throws NotFoundException If the user to delete is not found.
     */
    @Transactional
    override fun deleteUser(token: String, userId: Long) {
        val currentUserClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Ensure the user is an ADMIN
        if (currentUserClaims.role != Role.ADMIN) {
            throw IllegalAccessException("Access Denied. Only ADMIN can delete users.")
        }

        val userToDelete = userRepository.findById(userId)
            .orElseThrow { NotFoundException("User not found") }

        // Ensure the user has a role of AUTHOR or READER
        if (userToDelete.role == Role.ADMIN) {
            throw IllegalAccessException("Cannot delete ADMIN users.")
        }

        userRepository.delete(userToDelete)
        logger.info("User with ID $userId deleted successfully")
    }

    /**
     * Retrieves a user by their ID. Accessible to the user themselves or an ADMIN.
     *
     * @param token The authorization token of the currently authenticated user.
     * @param userId The ID of the user to retrieve.
     * @return The user response.
     * @throws IllegalAccessException If the user is not the requested user or an ADMIN.
     * @throws NotFoundException If the user is not found.
     */
    @Transactional
    override fun getUserById(token: String, userId: Long): UserResponse {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Ensure the user is either the target user or an ADMIN
        if (userClaims.id != userId && userClaims.role != Role.ADMIN) {
            throw IllegalAccessException("Access Denied. You can only view your own profile or must be an ADMIN.")
        }

        val user = userRepository.findById(userId)
            .orElseThrow { NotFoundException("User not found with id : $userId") }

        return user.toUserResponseDto()
    }

    /**
     * Retrieves the current user's details using the token.
     *
     * @param token The authorization token of the currently authenticated user.
     * @return The user response.
     * @throws NotFoundException If the user is not found.
     */
    @Transactional
    override fun getUserByToken(token: String): UserResponse {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        return userRepository.findById(userClaims.id).orElseThrow {
            NotFoundException("User not found")
        }.toUserResponseDto()
    }
}
