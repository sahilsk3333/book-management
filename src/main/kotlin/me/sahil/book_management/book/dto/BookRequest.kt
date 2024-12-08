package me.sahil.book_management.book.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size


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

