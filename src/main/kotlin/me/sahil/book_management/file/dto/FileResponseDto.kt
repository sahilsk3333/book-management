package me.sahil.book_management.file.dto

import java.time.LocalDateTime

data class FileResponseDto(

    val id: Long,

    val fileName: String,

    val mimeType: String,

    val downloadUrl: String,

    val createdAt: LocalDateTime
)
