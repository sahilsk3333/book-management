package me.sahil.book_management.book.service

import me.sahil.book_management.book.dto.BookPartialUpdateRequest
import me.sahil.book_management.book.dto.BookRequest
import me.sahil.book_management.book.dto.BookResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Service interface for managing [Book] entities.
 *
 * This interface defines methods for CRUD operations related to books, including retrieving,
 * adding, updating, and deleting books. It also provides functionality for partial updates
 * to books and for fetching books based on the author's ID. The service methods handle the
 * logic for validating and processing book-related data.
 */
interface BookService {

    /**
     * Get all books with pagination support.
     *
     * This method retrieves a paginated list of all books in the database.
     *
     * @param pageable Pagination information, such as page number and page size.
     * @return A [Page] of [BookResponse] objects representing the books.
     */
    fun getAllBooks(pageable: Pageable): Page<BookResponse>

    /**
     * Add a new book.
     *
     * This method adds a new book to the database. The book information is provided in the
     * [bookRequestDto], and the user's authentication token is validated during the operation.
     *
     * @param token The authorization token of the user performing the operation.
     * @param bookRequestDto The details of the book to be added.
     * @return A [BookResponse] representing the newly created book.
     */
    fun addBook(token: String, bookRequestDto: BookRequest): BookResponse

    /**
     * Delete a book by its ID.
     *
     * This method deletes the book with the specified ID from the database. The user's
     * authorization token is validated during the operation.
     *
     * @param token The authorization token of the user performing the operation.
     * @param bookId The ID of the book to be deleted.
     */
    fun deleteBook(token: String, bookId: Long)

    /**
     * Update a book by its ID.
     *
     * This method updates an existing book in the database with new information provided in
     * the [bookRequestDto]. The user's authorization token is validated during the operation.
     *
     * @param token The authorization token of the user performing the operation.
     * @param bookId The ID of the book to be updated.
     * @param bookRequestDto The new details of the book.
     * @return A [BookResponse] representing the updated book.
     */
    fun updateBook(token: String, bookId: Long, bookRequestDto: BookRequest): BookResponse

    /**
     * Partially update a book by its ID.
     *
     * This method allows partial updates to an existing book in the database. Only the fields
     * provided in the [requestDto] will be updated. The user's authorization token is validated
     * during the operation.
     *
     * @param token The authorization token of the user performing the operation.
     * @param bookId The ID of the book to be partially updated.
     * @param requestDto The details of the fields to be updated.
     * @return A [BookResponse] representing the updated book.
     */
    fun partialUpdateBook(token: String, bookId: Long, requestDto: BookPartialUpdateRequest): BookResponse

    /**
     * Get a book by its ID.
     *
     * This method retrieves the details of a book by its ID.
     *
     * @param bookId The ID of the book to retrieve.
     * @return A [BookResponse] representing the book.
     */
    fun getBookById(bookId: Long): BookResponse
}
