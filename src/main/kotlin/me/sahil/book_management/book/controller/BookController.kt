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

@RestController
@RequestMapping(ApiRoutes.BookRoutes.PATH)
class BookController(private val bookService: BookService) {

    // Get all books (paginated)
    @GetMapping(ApiRoutes.BookRoutes.GET_ALL_BOOKS)
    fun getAllBooks(pageable: Pageable): ResponseEntity<Page<BookResponse>> {
        return ResponseEntity.ok(bookService.getAllBooks(pageable))
    }

    // Add a new book
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

    // Delete a book
    @DeleteMapping(ApiRoutes.BookRoutes.DELETE_BOOK)
    fun deleteBook(
        @RequestHeader("Authorization") token: String,
        @PathVariable bookId: Long
    ): ResponseEntity<String> {
        bookService.deleteBook(token.extractBearerToken(), bookId)
        return ResponseEntity.ok("Book with ID $bookId has been deleted.")
    }

    // Update a book
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

    @GetMapping(ApiRoutes.BookRoutes.GET_BOOK)
    fun getBook(@PathVariable bookId: Long): ResponseEntity<BookResponse> {
        val bookResponseDto = bookService.getBookById(bookId)
        return ResponseEntity.ok(bookResponseDto)
    }

}
