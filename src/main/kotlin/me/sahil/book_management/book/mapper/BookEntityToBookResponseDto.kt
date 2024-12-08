package me.sahil.book_management.book.mapper

import me.sahil.book_management.user.mapper.toUserResponseDto
import me.sahil.book_management.book.dto.BookResponse
import me.sahil.book_management.book.entity.Book

/**
 * Extension function to convert a [Book] entity to a [BookResponse] DTO.
 *
 * This function takes a [Book] entity and maps its properties to a [BookResponse] DTO,
 * which is typically used for returning book data in API responses.
 * The author of the book is also transformed from a [User] entity to a [UserResponse] DTO.
 *
 * @return A [BookResponse] DTO containing the book's data.
 */
fun Book.toBookResponseDto(): BookResponse {
    return BookResponse(
        id = this.id,
        author = this.author.toUserResponseDto(),
        name = this.name,
        description = this.description,
        pdfUrl = this.pdfUrl,
        isbn = this.isbn,
        createdAt = this.createdAt
    )
}