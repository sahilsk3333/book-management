package me.sahil.book_management.file.service

import me.sahil.book_management.file.Entity.File
import me.sahil.book_management.file.dto.FileResponseDto
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

interface FileService {

    fun uploadFile(token: String, file: MultipartFile): FileResponseDto

    fun markFileAsUsed(fileId: Long)

    fun getUserFiles(token: String): List<FileResponseDto>

    fun getFileByUrl(downloadUrl: String): FileResponseDto?

    fun deleteFile(token: String, fileId: Long)

    fun getFileStoragePath(fileName: String): Path

    fun cleanUpUnusedFiles()
}