package me.sahil.book_management.book.service

import me.sahil.book_management.book.dto.BookPartialUpdateRequest
import me.sahil.book_management.book.dto.BookRequest
import me.sahil.book_management.book.dto.BookResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BookService {

    fun getAllBooks(pageable: Pageable): Page<BookResponse>
    fun addBook(token: String, bookRequestDto: BookRequest): BookResponse

    fun deleteBook(token: String, bookId: Long)

    fun updateBook(token: String, bookId: Long, bookRequestDto: BookRequest): BookResponse

    fun partialUpdateBook(token: String, bookId: Long, requestDto: BookPartialUpdateRequest): BookResponse

    fun getBookById(bookId: Long): BookResponse
}