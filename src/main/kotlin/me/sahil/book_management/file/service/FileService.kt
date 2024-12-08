package me.sahil.book_management.file.service

import me.sahil.book_management.auth.security.JwtTokenProvider
import me.sahil.book_management.file.Entity.File
import me.sahil.book_management.file.FileStorageUtil
import me.sahil.book_management.file.dto.FileResponseDto
import me.sahil.book_management.file.repository.FileRepository
import me.sahil.book_management.file.repository.toFileResponseDto
import me.sahil.book_management.user.entity.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

@Service
class FileService(
    private val fileRepository: FileRepository,
    private val fileStorageUtil: FileStorageUtil,
    private val jwtTokenProvider: JwtTokenProvider
) {

    // Upload a file
    @Transactional
    fun uploadFile(token: String, file: MultipartFile): File {
        // Get user from token
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Store the file (you can add validation here for file type, size, etc.)
        val fileName = fileStorageUtil.storeFile(file)

        // Create and save the file entity
        val newFile = File(
            fileName = fileName,
            mimeType = file.contentType ?: "application/octet-stream",
            user = User(userClaims.id),  // Link the file to the user
            downloadUrl = "http://localhost:8080/api/files/download/$fileName",  // Generate download URL
            isUsed = false
        )

        return fileRepository.save(newFile)
    }

    // Mark a file as used
    @Transactional
    fun markFileAsUsed(fileId: Long) {
        val file = fileRepository.findById(fileId).orElseThrow { IllegalArgumentException("File not found") }
        fileRepository.save(file.copy(isUsed = true))
    }

    // Get files by user (based on JWT)
    fun getUserFiles(token: String): List<FileResponseDto> {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)
        return fileRepository.findByUserId(userClaims.id).map { it.toFileResponseDto() }
    }

    // Get a file by download URL
    fun getFileByUrl(downloadUrl: String): File? {
        return fileRepository.findByDownloadUrl(downloadUrl)
    }

    // Periodic cleanup for unused files
    @Transactional
    fun cleanUpUnusedFiles() {
        val unusedFiles = fileRepository.findByIsUsedFalse()
        unusedFiles.forEach { file ->
            fileStorageUtil.deleteFile(file.fileName)  // Delete the physical file
            fileRepository.delete(file)  // Delete the file record from the database
        }
    }

    // Delete a file
    @Transactional
    fun deleteFile(token: String, fileId: Long) {
        val file = fileRepository.findById(fileId).orElseThrow { IllegalArgumentException("File not found") }
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Ensure that the file belongs to the current user
        if (file.user.id != userClaims.id) {
            throw IllegalAccessException("You can only delete your own files")
        }

        // Delete the physical file and the record from the database
        fileStorageUtil.deleteFile(file.fileName)
        fileRepository.delete(file)
    }

    // Get the path of a file stored on the server
    fun getFileStoragePath(fileName: String): Path {
        return fileStorageUtil.getFileStoragePath(fileName)
    }
}
