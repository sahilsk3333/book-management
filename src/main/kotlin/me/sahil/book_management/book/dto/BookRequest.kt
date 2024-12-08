package me.sahil.book_management.book.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * Data class representing a request to add or update a book.
 *
 * This class is used for creating or updating book records. It includes fields for the book's
 * name, description, PDF URL, and ISBN. Each field has validation rules to ensure the integrity of the data.
 *
 * @property name The name of the book. It must be non-empty, and its length cannot exceed 255 characters.
 * @property description A brief description of the book. It is optional and cannot exceed 1000 characters.
 * @property pdfUrl The URL for the PDF version of the book. It is optional.
 * @property isbn The ISBN of the book. It must be a valid ISBN-10 or ISBN-13 format.
 *
 * @throws IllegalArgumentException if any validation constraint is violated.
 */
data class BookRequest(
    @field:NotBlank(message = "Name is required") // Ensures the name is not empty or null
    @field:Size(max = 255, message = "Name cannot be longer than 255 characters") // Optional: Limit name length
    val name: String,

    @field:Size(max = 1000, message = "Description cannot be longer than 1000 characters") // Optional: Limit description length
    val description: String?,

    val pdfUrl: String?,

    @field:NotBlank(message = "ISBN is required") // Ensures ISBN is not empty or null
    @field:Pattern(
        regexp = "^(?:ISBN(?:-13)?:? )?(?=[0-9]{13}\$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}\$)97[89][- ]?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9]\$", // Regex for both ISBN-13 and ISBN-10
        message = "Invalid ISBN format, must be either ISBN-10 or ISBN-13"
    )
    val isbn: String
)

