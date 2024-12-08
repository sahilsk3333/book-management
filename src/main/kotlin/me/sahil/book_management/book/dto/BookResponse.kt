package me.sahil.book_management.book.dto

import com.fasterxml.jackson.annotation.JsonInclude
import me.sahil.book_management.user.dto.UserResponse
import java.time.ZonedDateTime

/**
 * Data class representing the response format for a book.
 *
 * This class is used to return book details in the response. It includes the book's ID, author information,
 * name, description, PDF URL, and ISBN. The `author` field is nullable and represents the user associated
 * with the book, while other fields may be null if not set.
 *
 * @property id The unique identifier of the book.
 * @property author The author of the book, represented by a `UserResponse`. This field can be null if the author is not available.
 * @property name The name of the book.
 * @property description The description of the book. It can be null if no description is provided.
 * @property pdfUrl The URL of the book's PDF version. This field can be null if not available.
 * @property isbn The ISBN of the book, which is required.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class BookResponse(
    val id: Long,
    val author: UserResponse?,
    val name: String,
    val description: String?,
    val pdfUrl: String?,
    val isbn: String,
    val createdAt: ZonedDateTime
)
