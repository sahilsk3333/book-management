package me.sahil.book_management.book.repository

import me.sahil.book_management.book.entity.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Page

interface BookRepository : JpaRepository<Book, Long> {
    fun findByAuthorId(authorId: Long, pageable: Pageable): Page<Book>
}
