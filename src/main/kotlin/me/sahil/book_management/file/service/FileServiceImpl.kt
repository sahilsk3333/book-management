package me.sahil.book_management.file.service

import me.sahil.book_management.auth.security.JwtTokenProvider
import me.sahil.book_management.core.exception.NotFoundException
import me.sahil.book_management.file.Entity.File
import me.sahil.book_management.file.util.FileStorageUtil
import me.sahil.book_management.file.dto.FileResponseDto
import me.sahil.book_management.file.repository.FileRepository
import me.sahil.book_management.file.mapper.toFileResponseDto
import me.sahil.book_management.user.entity.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

@Service
class FileServiceImpl(
    private val fileRepository: FileRepository,
    private val fileStorageUtil: FileStorageUtil,
    private val jwtTokenProvider: JwtTokenProvider
) : FileService {

    // Upload a file
    @Transactional
    override fun uploadFile(token: String, file: MultipartFile): FileResponseDto {
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

        return fileRepository.save(newFile).toFileResponseDto()
    }

    // Mark a file as used
    @Transactional
    override fun markFileAsUsed(fileId: Long) {
        val file = fileRepository.findById(fileId).orElseThrow { NotFoundException("File not found with id: $fileId") }
        fileRepository.save(file.copy(isUsed = true))
    }

    // Get files by user (based on JWT)
    override fun getUserFiles(token: String): List<FileResponseDto> {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)
        return fileRepository.findByUserId(userClaims.id).map { it.toFileResponseDto() }
    }

    // Get a file by download URL
    override fun getFileByUrl(downloadUrl: String): FileResponseDto? {
        return fileRepository.findByDownloadUrl(downloadUrl)?.toFileResponseDto()
    }

    // Periodic cleanup for unused files
    @Transactional
    override fun cleanUpUnusedFiles() {
        val unusedFiles = fileRepository.findByIsUsedFalse()
        unusedFiles.forEach { file ->
            fileStorageUtil.deleteFile(file.fileName)  // Delete the physical file
            fileRepository.delete(file)  // Delete the file record from the database
        }
    }

    // Delete a file
    @Transactional
    override fun deleteFile(token: String, fileId: Long) {
        val file = fileRepository.findById(fileId).orElseThrow { NotFoundException("File not found with id: $fileId") }
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
    override fun getFileStoragePath(fileName: String): Path {
        return fileStorageUtil.getFileStoragePath(fileName)
    }
}
