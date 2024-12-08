package me.sahil.book_management.file.dto

import java.time.ZonedDateTime


/**
 * Data transfer object (DTO) for representing a file response.
 *
 * This DTO is used to encapsulate the details of a file returned in responses, such as its
 * name, MIME type, download URL, and creation time. It is typically used in scenarios where
 * file metadata needs to be communicated to the client.
 *
 * @param id The unique identifier of the file.
 * @param fileName The name of the file.
 * @param mimeType The MIME type of the file (e.g., "image/png", "application/pdf").
 * @param downloadUrl The URL where the file can be downloaded.
 * @param createdAt The timestamp representing when the file was created or uploaded.
 */
data class FileResponseDto(

    val id: Long,

    val fileName: String,

    val mimeType: String,

    val downloadUrl: String,

    val createdAt: ZonedDateTime
)
