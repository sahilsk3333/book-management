package me.sahil.book_management.book.dto

/**
 * Data class representing a partial update request for a book.
 *
 * This class holds the fields that can be partially updated for an existing book.
 * Any field in this class can be `null`, which indicates that the field should not be updated.
 *
 * @property name The name of the book. It can be `null` if the name is not being updated.
 * @property description A brief description of the book. It can be `null` if the description is not being updated.
 * @property pdfUrl The URL for the PDF version of the book. It can be `null` if the PDF URL is not being updated.
 * @property isbn The ISBN number of the book. It can be `null` if the ISBN is not being updated.
 */
data class BookPartialUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val pdfUrl: String? = null,
    val isbn: String? = null
)
