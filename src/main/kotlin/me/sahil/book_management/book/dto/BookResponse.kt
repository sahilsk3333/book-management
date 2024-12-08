package me.sahil.book_management.book.dto

import com.fasterxml.jackson.annotation.JsonInclude
import me.sahil.book_management.user.dto.UserResponse

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BookResponse(
    val id: Long,
    val author: UserResponse?,
    val name: String,
    val description: String?,
    val pdfUrl: String?,
    val isbn: String
)
