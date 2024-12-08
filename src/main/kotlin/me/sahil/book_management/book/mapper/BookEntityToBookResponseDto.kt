package me.sahil.book_management.book.mapper

import me.sahil.book_management.user.mapper.toUserResponseDto
import me.sahil.book_management.book.dto.BookResponse
import me.sahil.book_management.book.entity.Book

fun Book.toBookResponseDto(): BookResponse {
    return BookResponse(
        id = this.id,
        author = this.author.toUserResponseDto(),
        name = this.name,
        description = this.description,
        pdfUrl = this.pdfUrl,
        isbn = this.isbn
    )
}