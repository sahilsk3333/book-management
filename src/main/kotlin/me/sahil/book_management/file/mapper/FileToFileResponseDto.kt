package me.sahil.book_management.file.mapper

import me.sahil.book_management.file.Entity.File
import me.sahil.book_management.file.dto.FileResponseDto

/**
 * Extension function to convert a `File` entity to a `FileResponseDto` object.
 *
 * This function maps the properties of a `File` entity to a `FileResponseDto`, which is
 * a DTO (Data Transfer Object) used to send file details in the API response.
 * The `FileResponseDto` contains only the relevant details required for the client-side
 * (such as file ID, file name, download URL, MIME type, and creation timestamp).
 *
 * @return A `FileResponseDto` instance containing the file details.
 */
fun File.toFileResponseDto() = FileResponseDto(
    id = this.id,
    fileName = this.fileName,
    downloadUrl = this.downloadUrl,
    mimeType = this.mimeType,
    createdAt = this.createdAt
)