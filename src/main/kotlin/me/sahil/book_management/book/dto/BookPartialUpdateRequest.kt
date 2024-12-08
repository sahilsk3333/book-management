package me.sahil.book_management.book.dto

data class BookPartialUpdateRequest(
    val name: String?,
    val description: String?,
    val pdfUrl: String?,
    val isbn: String?
)
