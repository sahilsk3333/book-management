package me.sahil.book_management.file.mapper

import me.sahil.book_management.file.Entity.File
import me.sahil.book_management.file.dto.FileResponseDto

fun File.toFileResponseDto() = FileResponseDto(
    id = this.id,
    fileName = this.fileName,
    downloadUrl = this.downloadUrl,
    mimeType = this.mimeType,
    createdAt = this.createdAt
)