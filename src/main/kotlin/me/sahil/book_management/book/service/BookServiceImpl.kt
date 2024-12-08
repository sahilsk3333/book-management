package me.sahil.book_management.book.service

import jakarta.transaction.Transactional
import me.sahil.book_management.user.entity.User
import me.sahil.book_management.auth.security.JwtTokenProvider
import me.sahil.book_management.book.dto.BookPartialUpdateRequest
import me.sahil.book_management.book.dto.BookRequest
import me.sahil.book_management.book.dto.BookResponse
import me.sahil.book_management.book.entity.Book
import me.sahil.book_management.book.mapper.toBookResponseDto
import me.sahil.book_management.book.repository.BookRepository
import me.sahil.book_management.core.exception.NotFoundException
import me.sahil.book_management.core.role.Role
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class BookServiceImpl(
    private val bookRepository: BookRepository,
    private val jwtTokenProvider: JwtTokenProvider
) : BookService {

    // List all books (paginated)
    override fun getAllBooks(pageable: Pageable): Page<BookResponse> {
        return bookRepository.findAll(pageable).map { it.toBookResponseDto() }
    }

    // Add a new book (only AUTHORS can add)
    override fun addBook(token: String, bookRequestDto: BookRequest): BookResponse {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Ensure the role is AUTHOR
        if (userClaims.role != Role.AUTHOR) {
            throw IllegalAccessException("Only AUTHORS can add books.")
        }

        // Check if the ISBN is already taken by another book
        if (bookRepository.existsByIsbn(bookRequestDto.isbn)) {
            throw IllegalArgumentException("The ISBN is already taken by another book.")
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
    override fun deleteBook(token: String, bookId: Long) {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Fetch the book to be deleted
        val book = bookRepository.findById(bookId).orElseThrow {
            NotFoundException("Book not found with id: $bookId")
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
    override fun updateBook(token: String, bookId: Long, bookRequestDto: BookRequest): BookResponse {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Fetch the book to be updated
        val book = bookRepository.findById(bookId).orElseThrow {
            NotFoundException("Book not found with id: $bookId")
        }

        // Check if the user is the author of the book
        if (book.author.id != userClaims.id) {
            throw IllegalAccessException("You can only edit your own books.")
        }

        // Check if the ISBN is already taken by another book (excluding the current book)
        if (bookRepository.existsByIsbnAndIdNot(bookRequestDto.isbn, bookId)) {
            throw IllegalArgumentException("The ISBN is already taken by another book.")
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


    @Transactional
    override fun partialUpdateBook(token: String, bookId: Long, requestDto: BookPartialUpdateRequest): BookResponse {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Fetch the book to be updated
        val book = bookRepository.findById(bookId).orElseThrow {
            NotFoundException("Book not found with id: $bookId")
        }

        // Check if the user is the author of the book
        if (book.author.id != userClaims.id) {
            throw IllegalAccessException("You can only edit your own books.")
        }

        // Check if the ISBN is part of the update and if it's unique
        requestDto.isbn?.let {
            if (bookRepository.existsByIsbnAndIdNot(it, bookId)) {
                throw IllegalArgumentException("The ISBN is already taken by another book.")
            }
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

    override fun getBookById(bookId: Long): BookResponse {
        // Fetch the book by ID from the repository
        val book = bookRepository.findById(bookId).orElseThrow {
            NotFoundException("Book not found with id: $bookId")
        }

        // Return the book as a response DTO
        return book.toBookResponseDto()
    }


}
