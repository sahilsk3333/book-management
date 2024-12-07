package me.sahil.book_management.book.service

import jakarta.transaction.Transactional
import me.sahil.book_management.auth.entity.User
import me.sahil.book_management.auth.security.JwtTokenProvider
import me.sahil.book_management.book.dto.BookPartialUpdateRequestDto
import me.sahil.book_management.book.dto.BookRequestDto
import me.sahil.book_management.book.dto.BookResponseDto
import me.sahil.book_management.book.entity.Book
import me.sahil.book_management.book.mapper.toBookResponseDto
import me.sahil.book_management.book.repository.BookRepository
import me.sahil.book_management.common.role.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    // List all books (paginated)
    fun getAllBooks(pageable: Pageable): Page<BookResponseDto> {
        return bookRepository.findAll(pageable).map { it.toBookResponseDto() }
    }

    // Add a new book (only AUTHORS can add)
    fun addBook(token: String, bookRequestDto: BookRequestDto): BookResponseDto {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Ensure the role is AUTHOR
        if (userClaims.role != Role.AUTHOR) {
            throw IllegalAccessException("Only AUTHORS can add books.")
        }

        val book = Book(
            author = User(userClaims.id),
            name = bookRequestDto.name,
            description = bookRequestDto.description,
            pdfUrl = bookRequestDto.pdfUrl,
            isbn = bookRequestDto.isbn
        )

        return bookRepository.save(book).toBookResponseDto().copy(author = null)
    }

    // Delete a book (Admins and authors only)
    @Transactional
    fun deleteBook(token: String, bookId: Long) {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Fetch the book to be deleted
        val book = bookRepository.findById(bookId).orElseThrow {
            IllegalArgumentException("Book not found.")
        }

        // Check if the user is authorized to delete this book (Admin can delete any book, Author can only delete their own)
        if (userClaims.role == Role.READER) {
            throw IllegalAccessException("You are not allowed to delete a book as a READER.")
        }

        // Check if the user is an AUTHOR and ensure they are the author of the book
        if (userClaims.role == Role.AUTHOR && book.author.id != userClaims.id) {
            throw IllegalAccessException("You are not authorized to delete this book.")
        }

        // Delete the book
        bookRepository.delete(book)
    }

    // Update a book (Authors can only edit their own books)
    @Transactional
    fun updateBook(token: String, bookId: Long, bookRequestDto: BookRequestDto): BookResponseDto {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Fetch the book to be updated
        val book = bookRepository.findById(bookId).orElseThrow {
            IllegalArgumentException("Book not found.")
        }

        // Check if the user is the author of the book
        if (book.author.id != userClaims.id) {
            throw IllegalAccessException("You can only edit your own books.")
        }

        // Update the book with the new details
        val updatedBook = book.copy(
            name = bookRequestDto.name,
            description = bookRequestDto.description,
            pdfUrl = bookRequestDto.pdfUrl,
            isbn = bookRequestDto.isbn
        )

        // Save and return the updated book response
        return bookRepository.save(updatedBook).toBookResponseDto()
    }


    // Partial update of a book (Authors can only partially update their own books)
    @Transactional
    fun partialUpdateBook(token: String, bookId: Long, requestDto: BookPartialUpdateRequestDto): BookResponseDto {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Fetch the book to be updated
        val book = bookRepository.findById(bookId).orElseThrow {
            IllegalArgumentException("Book not found.")
        }

        // Check if the user is the author of the book
        if (book.author.id != userClaims.id) {
            throw IllegalAccessException("You can only edit your own books.")
        }

        val updatedBook = book.copy(
            name = requestDto.name ?: book.name,
            isbn = requestDto.isbn ?: book.isbn,
            description = requestDto.description ?: book.description,
            pdfUrl = requestDto.pdfUrl ?: book.pdfUrl
        )

        // Save and return the updated book response
        return bookRepository.save(updatedBook).toBookResponseDto()
    }

    fun getBookById(bookId: Long): BookResponseDto {
        // Fetch the book by ID from the repository
        val book = bookRepository.findById(bookId).orElseThrow {
            IllegalArgumentException("Book not found.")
        }

        // Return the book as a response DTO
        return book.toBookResponseDto()
    }



}
