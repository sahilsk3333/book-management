package me.sahil.book_management.book.controller

import jakarta.validation.Valid
import me.sahil.book_management.book.dto.BookPartialUpdateRequest
import me.sahil.book_management.book.dto.BookRequest
import me.sahil.book_management.book.dto.BookResponse
import me.sahil.book_management.book.service.BookService
import me.sahil.book_management.core.route.ApiRoutes
import me.sahil.book_management.core.utils.extractBearerToken
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*

/**
 * Controller for managing books.
 *
 * This controller provides endpoints for handling CRUD operations on books, including
 * retrieving, adding, updating, and deleting books. It also supports partial updates
 * and pagination for retrieving all books.
 *
 * @param bookService The service used to handle the business logic for book operations.
 */
@RestController
@RequestMapping(ApiRoutes.BookRoutes.PATH)
class BookController(private val bookService: BookService) {

    /**
     * Retrieves all books with pagination support.
     *
     * This method fetches all books from the database with pagination. It uses the
     * [Pageable] interface to handle page requests.
     *
     * @param pageable The pagination parameters for the request (page number, page size).
     * @return A paginated list of [BookResponse] wrapped in a [ResponseEntity].
     */
    @GetMapping(ApiRoutes.BookRoutes.GET_ALL_BOOKS)
    fun getAllBooks(pageable: Pageable): ResponseEntity<Page<BookResponse>> {
        return ResponseEntity.ok(bookService.getAllBooks(pageable))
    }

    /**
     * Adds a new book to the system.
     *
     * This method allows adding a new book. The book's details are passed in the
     * [bookRequestDto], and the method validates the request body. A JWT token is
     * expected in the `Authorization` header to authenticate the request.
     *
     * @param token The authorization token of the user making the request.
     * @param bookRequestDto The details of the book to be added.
     * @param result The binding result to capture any validation errors.
     * @return A [BookResponse] representing the newly added book.
     * @throws BindException If there are validation errors in the request body.
     */
    @PostMapping(ApiRoutes.BookRoutes.ADD_BOOK)
    fun addBook(
        @RequestHeader("Authorization") token: String,
        @Valid @RequestBody bookRequestDto: BookRequest,
        result: BindingResult
    ): ResponseEntity<BookResponse> {
        if (result.hasErrors()) {
            throw BindException(result)
        }
        val addedBook = bookService.addBook(token.extractBearerToken(), bookRequestDto)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(addedBook)
    }

    /**
     * Deletes a book by its ID.
     *
     * This method deletes a book with the specified `bookId`. A valid authorization
     * token is required in the `Authorization` header to perform this operation.
     *
     * @param token The authorization token of the user making the request.
     * @param bookId The ID of the book to be deleted.
     * @return A confirmation message stating that the book has been deleted.
     */
    @DeleteMapping(ApiRoutes.BookRoutes.DELETE_BOOK)
    fun deleteBook(
        @RequestHeader("Authorization") token: String,
        @PathVariable bookId: Long
    ): ResponseEntity<Map<String, String>> {
        bookService.deleteBook(token.extractBearerToken(), bookId)
        val response = mapOf("message" to "Book with ID $bookId has been deleted.")
        return ResponseEntity.ok(response)
    }

    /**
     * Updates a book's details.
     *
     * This method updates the book with the specified `bookId`. The book's new details
     * are provided in the [bookRequestDto], and the request body is validated. A JWT
     * token is required in the `Authorization` header for authentication.
     *
     * @param token The authorization token of the user making the request.
     * @param bookId The ID of the book to be updated.
     * @param bookRequestDto The new details of the book.
     * @param result The binding result to capture any validation errors.
     * @return A [BookResponse] representing the updated book.
     * @throws BindException If there are validation errors in the request body.
     */
    @PutMapping(ApiRoutes.BookRoutes.UPDATE_BOOK)
    fun updateBook(
        @RequestHeader("Authorization") token: String,
        @PathVariable bookId: Long,
        @Valid @RequestBody bookRequestDto: BookRequest,
        result: BindingResult
    ): ResponseEntity<BookResponse> {
        if (result.hasErrors()) {
            throw BindException(result)
        }
        return ResponseEntity.ok(
            bookService.updateBook(token.extractBearerToken(), bookId, bookRequestDto)
        )
    }

    /**
     * Partially updates a book's details.
     *
     * This method allows for partial updates to a book. Only the fields provided in
     * the [bookPartialUpdateRequestDto] are updated. The book's ID is specified in
     * the URL, and a JWT token is required in the `Authorization` header for authentication.
     *
     * @param token The authorization token of the user making the request.
     * @param bookId The ID of the book to be partially updated.
     * @param bookPartialUpdateRequestDto The partial update details of the book.
     * @return A [BookResponse] representing the updated book.
     */
    @PatchMapping(ApiRoutes.BookRoutes.PATCH_BOOK)
    fun partialUpdateBook(
        @RequestHeader("Authorization") token: String,
        @PathVariable bookId: Long,
        @RequestBody bookPartialUpdateRequestDto: BookPartialUpdateRequest
    ): ResponseEntity<BookResponse> {
        val updatedBook =
            bookService.partialUpdateBook(token.extractBearerToken(), bookId, bookPartialUpdateRequestDto)
        return ResponseEntity.ok(updatedBook)
    }

    /**
     * Retrieves a specific book by its ID.
     *
     * This method retrieves a book's details by its `bookId`. It returns a
     * [BookResponse] containing the book's information.
     *
     * @param bookId The ID of the book to retrieve.
     * @return A [BookResponse] representing the requested book.
     */
    @GetMapping(ApiRoutes.BookRoutes.GET_BOOK)
    fun getBook(@PathVariable bookId: Long): ResponseEntity<BookResponse> {
        val bookResponseDto = bookService.getBookById(bookId)
        return ResponseEntity.ok(bookResponseDto)
    }
}

