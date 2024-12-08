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

/**
 * Implementation of the [BookService] interface for managing book-related operations.
 *
 * This class contains the business logic for performing CRUD operations on books. It includes
 * methods for listing, adding, updating, and deleting books. The class also supports partial
 * updates to books and retrieves book details by ID. Authorization checks are performed based
 * on the user's role, ensuring that only authorized users (i.e., authors and admins) can
 * modify books. The `JwtTokenProvider` is used to extract the user details from the token.
 */
@Service
class BookServiceImpl(
    private val bookRepository: BookRepository,
    private val jwtTokenProvider: JwtTokenProvider
) : BookService {

    /**
     * List all books with pagination support.
     *
     * Retrieves a paginated list of all books from the database and maps them to a
     * [BookResponse] DTO.
     *
     * @param pageable Pagination information, such as page number and page size.
     * @return A [Page] of [BookResponse] objects representing the books.
     */
    override fun getAllBooks(pageable: Pageable): Page<BookResponse> {
        return bookRepository.findAll(pageable).map { it.toBookResponseDto() }
    }

    /**
     * Add a new book to the system.
     *
     * Only authors can add new books. The user's role is validated using the JWT token,
     * and the ISBN is checked to ensure it is unique. A new [Book] is created and saved to
     * the repository, returning the corresponding [BookResponse] DTO.
     *
     * @param token The JWT authorization token of the user.
     * @param bookRequestDto The details of the book to be added.
     * @return A [BookResponse] representing the newly created book.
     */
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

    /**
     * Delete a book from the system.
     *
     * Both authors and admins can delete books. Authors can only delete their own books,
     * and admins can delete any book. The user's role is validated, and if authorized,
     * the book is deleted from the repository.
     *
     * @param token The JWT authorization token of the user.
     * @param bookId The ID of the book to be deleted.
     */
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

    /**
     * Update an existing book.
     *
     * Only authors can update their own books, and admins can update any book. The method
     * validates the user's role and ownership of the book. Additionally, it checks that
     * the new ISBN is unique (excluding the current book). The book details are updated
     * and saved to the repository, returning the updated [BookResponse].
     *
     * @param token The JWT authorization token of the user.
     * @param bookId The ID of the book to be updated.
     * @param bookRequestDto The updated details of the book.
     * @return A [BookResponse] representing the updated book.
     */
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

    /**
     * Partially update a book.
     *
     * Similar to full updates, but this method allows partial modifications of the book's
     * attributes. If the ISBN is updated, it is checked for uniqueness. Only authors can
     * update their own books, and the user's role is validated.
     *
     * @param token The JWT authorization token of the user.
     * @param bookId The ID of the book to be updated.
     * @param requestDto The details to be updated (can be partial).
     * @return A [BookResponse] representing the updated book.
     */
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

    /**
     * Get a book by its ID.
     *
     * Retrieves a book from the database by its ID and returns it as a [BookResponse] DTO.
     *
     * @param bookId The ID of the book to retrieve.
     * @return A [BookResponse] representing the book.
     */
    override fun getBookById(bookId: Long): BookResponse {
        // Fetch the book by ID from the repository
        val book = bookRepository.findById(bookId).orElseThrow {
            NotFoundException("Book not found with id: $bookId")
        }

        // Return the book as a response DTO
        return book.toBookResponseDto()
    }
}

