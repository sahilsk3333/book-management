package me.sahil.book_management.auth.service

import me.sahil.book_management.auth.entity.User
import me.sahil.book_management.auth.repository.UserRepository
import me.sahil.book_management.auth.security.JwtTokenProvider
import me.sahil.book_management.common.role.Role
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    // Get all users, excluding the admin itself
    @Transactional
    fun getAllUsers(token: String): List<User> {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Ensure the user is an ADMIN
        if (userClaims.role != Role.ADMIN) {
            throw IllegalAccessException("Access Denied. Only ADMIN can view the users list.")
        }

        // Exclude the admin user from the list of users
        return userRepository.findAllExcludingAdmin(userClaims.id)
    }

    // Delete a user, only allowed for ADMIN and only for users with AUTHOR or READER roles
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
}
