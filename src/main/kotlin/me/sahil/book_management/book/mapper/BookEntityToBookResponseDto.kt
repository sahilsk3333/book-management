package me.sahil.book_management.book.mapper

import me.sahil.book_management.user.mapper.toUserResponseDto
import me.sahil.book_management.book.dto.BookResponseDto
import me.sahil.book_management.book.entity.Book

fun Book.toBookResponseDto(): BookResponseDto {
    return BookResponseDto(
        id = this.id,
        author = this.author.toUserResponseDto(),
        name = this.name,
        description = this.description,
        pdfUrl = this.pdfUrl,
        isbn = this.isbn
    )
}