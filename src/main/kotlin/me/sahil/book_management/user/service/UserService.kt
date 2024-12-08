package me.sahil.book_management.user.service

import me.sahil.book_management.auth.security.JwtTokenProvider
import me.sahil.book_management.common.role.Role
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
class UserService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val fileRepository: FileRepository
) {

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    // Get all users (paginated), excluding the admin itself
    @Transactional
    fun getAllUsers(token: String, pageable: Pageable): Page<UserResponse> {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Ensure the user is an ADMIN
        if (userClaims.role != Role.ADMIN) {
            throw IllegalAccessException("Access Denied. Only ADMIN can view the users list.")
        }

        // Fetch users excluding the admin itself
        return userRepository
            .findAllExcludingAdmin(userClaims.id, pageable)
            .map { user ->
                user.toUserResponseDto()
            }
    }

    // Update user details (Only user can update themselves)
    @Transactional
    fun updateUser(token: String, userId: Long, updateUserRequestDto: UpdateUserRequest): UserResponse {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Ensure the user can only update their own profile
        if (userClaims.id != userId) {
            throw IllegalAccessException("You can only update your own profile.")
        }

        // Fetch the user to update
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        // Apply updates
        val updatedUser = user.copy(
            name = updateUserRequestDto.name,
            email = updateUserRequestDto.email,
            age = updateUserRequestDto.age,
            image = updateUserRequestDto.image,
            role = updateUserRequestDto.role
        )

        // If the image URL is provided, check if it exists in the file table and mark as used
        updatedUser.image?.let { imageUrl ->
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


        return userRepository.save(updatedUser).toUserResponseDto()
    }

    // Partial Update user details (Only user can update themselves)
    @Transactional
    fun updateUser(
        token: String,
        userId: Long,
        partialUpdateUserRequestDto: PartialUpdateUserRequest
    ): UserResponse {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Ensure the user can only update their own profile
        if (userClaims.id != userId) {
            throw IllegalAccessException("You can only update your own profile.")
        }

        // Fetch the user to update
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }


        // Apply updates
        val updatedUser = user.copy(
            name = partialUpdateUserRequestDto.name ?: user.name,
            email = partialUpdateUserRequestDto.email ?: user.email,
            age = partialUpdateUserRequestDto.age ?: user.age,
            image = partialUpdateUserRequestDto.image ?: user.image,
            role = partialUpdateUserRequestDto.role ?: user.role
        )

        partialUpdateUserRequestDto.image?.let { imageUrlForUpdate ->
            if (imageUrlForUpdate != user.image){
                val file = fileRepository.findByDownloadUrl(imageUrlForUpdate)
                if (file != null) {
                    // Mark the file as used
                    fileRepository.save(file.copy(
                        isUsed = true
                    ))
                    logger.info("Image URL $imageUrlForUpdate marked as used")
                } else {
                    logger.warn("No file found with the provided image URL: $imageUrlForUpdate")
                }
            }
        }

        return userRepository.save(updatedUser).toUserResponseDto()
    }


    // Delete a user (only allowed for ADMIN and only for AUTHOR or READER roles)
    @Transactional
    fun deleteUser(token: String, userId: Long) {
        val currentUserClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Ensure the user is an ADMIN
        if (currentUserClaims.role != Role.ADMIN) {
            throw IllegalAccessException("Access Denied. Only ADMIN can delete users.")
        }

        val userToDelete = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        // Ensure the user has a role of AUTHOR or READER
        if (userToDelete.role == Role.ADMIN) {
            throw IllegalAccessException("Cannot delete ADMIN users.")
        }

        userRepository.delete(userToDelete)
        logger.info("User with ID $userId deleted successfully")
    }


    // Fetch a user by ID (self-access or admin access only)
    @Transactional
    fun getUserById(token: String, userId: Long): UserResponse {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Check if the requester is the user themselves or an admin
        if (userClaims.id != userId && userClaims.role != Role.ADMIN) {
            throw IllegalAccessException("Access Denied. You can only view your own profile or must be an ADMIN.")
        }

        // Fetch the user from the database
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found.") }

        return user.toUserResponseDto()
    }

    @Transactional
    fun getUserByToken(token: String): UserResponse {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Fetch the user using the ID from the token
        return userRepository.findById(userClaims.id).orElseThrow {
            IllegalArgumentException("User not found.")
        }.toUserResponseDto()
    }


}
