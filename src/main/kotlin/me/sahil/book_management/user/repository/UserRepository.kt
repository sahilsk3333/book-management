package me.sahil.book_management.user.repository

import me.sahil.book_management.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * Repository interface for managing `User` entities.
 *
 * Provides methods to interact with the `User` table in the database.
 * The methods include operations for retrieving users by email, checking if an email exists,
 * and retrieving users excluding an admin.
 */
interface UserRepository : JpaRepository<User, Long> {

     /**
      * Finds a user by their email address.
      *
      * @param email the email address to search for
      * @return the user with the given email, or null if no user is found
      */
     fun findByEmail(email: String): User?

     /**
      * Checks if a user exists with the given email address.
      *
      * @param email the email address to check
      * @return true if a user exists with the given email, false otherwise
      */
     fun existsByEmail(email: String): Boolean

     /**
      * Retrieves all users except the one identified by the `excludeUserId`.
      * This method is typically used to exclude the admin user from a list of users.
      *
      * @param excludeUserId the ID of the user to exclude from the list
      * @param pageable pagination information to control the results
      * @return a paginated list of users excluding the one with the given ID
      */
     @Query(
          value = "SELECT u FROM User u WHERE u.id != :excludeUserId",
          countQuery = "SELECT COUNT(u) FROM User u WHERE u.id != :excludeUserId"
     )
     fun findAllExcludingAdmin(excludeUserId: Long, pageable: Pageable): Page<User>
}
