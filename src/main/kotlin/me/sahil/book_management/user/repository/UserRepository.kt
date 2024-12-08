package me.sahil.book_management.user.repository

import me.sahil.book_management.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, Long> {
     fun findByEmail(email: String): User?

     fun existsByEmail(email: String): Boolean // Check if the email already exists

     @Query(
          value = "SELECT u FROM User u WHERE u.id != :excludeUserId",
          countQuery = "SELECT COUNT(u) FROM User u WHERE u.id != :excludeUserId"
     )
     fun findAllExcludingAdmin(excludeUserId: Long, pageable: Pageable): Page<User>
}