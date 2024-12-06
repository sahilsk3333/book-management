package me.sahil.book_management.auth.repository

import me.sahil.book_management.auth.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, Long> {
     fun findByEmail(email: String): User?

     // Fetch all users excluding the provided admin
     @Query("SELECT u FROM User u WHERE u.id != :excludeUserId")
     fun findAllExcludingAdmin(excludeUserId: Long): List<User>
}
