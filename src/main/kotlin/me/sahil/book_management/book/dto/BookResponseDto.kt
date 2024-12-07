package me.sahil.book_management.book.dto

import com.fasterxml.jackson.annotation.JsonInclude
import me.sahil.book_management.auth.dto.UserResponseDto

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BookResponseDto(
    val id: Long,
    val author: UserResponseDto?,
    val name: String,
    val description: String?,
    val pdfUrl: String?,
    val isbn: String
)
