package me.sahil.book_management.book.controller

import me.sahil.book_management.book.dto.BookPartialUpdateRequest
import me.sahil.book_management.book.dto.BookRequest
import me.sahil.book_management.book.dto.BookResponse
import me.sahil.book_management.book.service.BookService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookController(private val bookService: BookService) {

    // Get all books (paginated)
    @GetMapping
    fun getAllBooks(pageable: Pageable): ResponseEntity<Page<BookResponse>> {
        return ResponseEntity.ok(bookService.getAllBooks(pageable))
    }

    // Add a new book
    @PostMapping
    fun addBook(
        @RequestHeader("Authorization") token: String,
        @RequestBody bookRequestDto: BookRequest
    ): ResponseEntity<BookResponse> {
        return ResponseEntity.ok(bookService.addBook(token.removePrefix("Bearer "), bookRequestDto))
    }

    // Delete a book
    @DeleteMapping("/{bookId}")
    fun deleteBook(
        @RequestHeader("Authorization") token: String,
        @PathVariable bookId: Long
    ): ResponseEntity<String> {
        bookService.deleteBook(token.removePrefix("Bearer "), bookId)
        return ResponseEntity.ok("Book with ID $bookId has been deleted.")
    }

    // Update a book
    @PutMapping("/{bookId}")
    fun updateBook(
        @RequestHeader("Authorization") token: String,
        @PathVariable bookId: Long,
        @RequestBody bookRequestDto: BookRequest
    ): ResponseEntity<BookResponse> {
        return ResponseEntity.ok(
            bookService.updateBook(token.removePrefix("Bearer "), bookId, bookRequestDto)
        )
    }

    @PatchMapping("/{bookId}")
    fun partialUpdateBook(
        @RequestHeader("Authorization") token: String,
        @PathVariable bookId: Long,
        @RequestBody bookPartialUpdateRequestDto: BookPartialUpdateRequest
    ): ResponseEntity<BookResponse> {
        val updatedBook =
            bookService.partialUpdateBook(token.removePrefix("Bearer "), bookId, bookPartialUpdateRequestDto)
        return ResponseEntity.ok(updatedBook)
    }

    @GetMapping("/{bookId}")
    fun getBook(@PathVariable id: Long): ResponseEntity<BookResponse> {
        val bookResponseDto = bookService.getBookById(id)
        return ResponseEntity.ok(bookResponseDto)
    }

}
