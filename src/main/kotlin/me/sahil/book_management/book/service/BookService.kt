package me.sahil.book_management.book.service

import me.sahil.book_management.book.entity.Book
import me.sahil.book_management.book.repository.BookRepository
import me.sahil.book_management.auth.entity.User
import me.sahil.book_management.auth.repository.UserRepository
import org.springframework.stereotype.Service


@Service
class BookService(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) {

    fun getAllBooksByAuthor(userId: Long): List<Book> {
        val user = userRepository.findById(userId).orElseThrow { Exception("Author not found") }
        return bookRepository.findByAuthor(user)
    }

    fun createBook(userId: Long, name: String, description: String, pdfUrl: String): Book {
        val user = userRepository.findById(userId).orElseThrow { Exception("Author not found") }
        val book = Book(author = user, name = name, description = description, pdfUrl = pdfUrl)
        return bookRepository.save(book)
    }
}
