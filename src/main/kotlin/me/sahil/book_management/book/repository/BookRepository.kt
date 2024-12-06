package me.sahil.book_management.book.repository

import me.sahil.book_management.book.entity.Book
import me.sahil.book_management.auth.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : JpaRepository<Book, Long> {

    // Find all books by the given author (user)
    fun findByAuthor(author: User): List<Book>

    // Optional: Find a book by its name (if needed)
    fun findByName(name: String): Book?

    // Optional: Find books by a part of the name (search functionality)
    fun findByNameContaining(name: String): List<Book>
}
