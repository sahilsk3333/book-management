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
/**
 * Implementation of the [FileService] interface that provides file management functionalities.
 *
 * This class contains methods for uploading, deleting, and retrieving files. It also handles marking files as used,
 * periodic cleanup of unused files, and storing files in a file storage system.
 *
 * @param fileRepository The repository for accessing file entities from the database.
 * @param fileStorageUtil Utility for handling file storage operations (e.g., storing, deleting, retrieving files).
 * @param jwtTokenProvider Provider for extracting user details from the JWT token.
 */
class FileServiceImpl(
    private val fileRepository: FileRepository,
    private val fileStorageUtil: FileStorageUtil,
    private val jwtTokenProvider: JwtTokenProvider
) : FileService {

    /**
     * Uploads a new file and stores it in the file storage system.
     *
     * This method processes the uploaded file, stores it in the appropriate location, and creates a new [File] entity
     * in the database, associating it with the user identified by the provided JWT token. The method also generates
     * a download URL for the uploaded file.
     *
     * @param token The JWT token used to authenticate and identify the user uploading the file.
     * @param serverBaseUrl The base URL of the server, used to generate the file's download URL.
     * @param file The file to be uploaded.
     * @return A [FileResponseDto] containing the file's metadata, including file name, MIME type, and the generated download URL.
     * @throws TokenInvalidException If the JWT token is invalid.
     */
    @Transactional
    override fun uploadFile(token: String, serverBaseUrl: String, file: MultipartFile): FileResponseDto {
        // Get user from token
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)

        // Store the file (you can add validation here for file type, size, etc.)
        val fileName = fileStorageUtil.storeFile(file)

        // Create and save the file entity
        val newFile = File(
            fileName = fileName,
            mimeType = file.contentType ?: "application/octet-stream",
            user = User(userClaims.id),  // Link the file to the user
            downloadUrl = "$serverBaseUrl/api/files/download/$fileName",  // Generate download URL
            isUsed = false
        )

        return fileRepository.save(newFile).toFileResponseDto()
    }

    /**
     * Marks a file as used.
     *
     * This method updates the `isUsed` status of the file to `true` in the database.
     *
     * @param fileId The ID of the file to mark as used.
     * @throws NotFoundException if the file is not found in the database.
     */
    @Transactional
    override fun markFileAsUsed(fileId: Long) {
        val file = fileRepository.findById(fileId).orElseThrow { NotFoundException("File not found with id: $fileId") }
        fileRepository.save(file.copy(isUsed = true))
    }

    /**
     * Retrieves all files uploaded by the current user.
     *
     * This method fetches files associated with the user identified by the provided JWT token.
     *
     * @param token The JWT token used to identify the current user.
     * @return A list of [FileResponseDto] objects containing metadata for each file uploaded by the user.
     * @throws TokenInvalidException if the JWT token is invalid.
     */
    override fun getUserFiles(token: String): List<FileResponseDto> {
        val userClaims = jwtTokenProvider.getUserDetailsFromToken(token)
        return fileRepository.findByUserId(userClaims.id).map { it.toFileResponseDto() }
    }

    /**
     * Retrieves a file based on its download URL.
     *
     * This method fetches the details of a file using its download URL.
     *
     * @param downloadUrl The download URL of the file.
     * @return A [FileResponseDto] containing the file's metadata, or `null` if no file is found with the given URL.
     */
    override fun getFileByUrl(downloadUrl: String): FileResponseDto? {
        return fileRepository.findByDownloadUrl(downloadUrl)?.toFileResponseDto()
    }

    /**
     * Performs periodic cleanup of unused files.
     *
     * This method deletes files that are marked as unused (`isUsed = false`) in the database and removes them from the file storage system.
     */
    @Transactional
    override fun cleanUpUnusedFiles() {
        val unusedFiles = fileRepository.findByIsUsedFalse()
        unusedFiles.forEach { file ->
            fileStorageUtil.deleteFile(file.fileName)  // Delete the physical file
            fileRepository.delete(file)  // Delete the file record from the database
        }
    }

    /**
     * Deletes a file based on its ID.
     *
     * This method ensures that the file belongs to the user making the request (authenticated via the JWT token).
     * It then deletes the physical file from the storage system and removes the file record from the database.
     *
     * @param token The JWT token used to identify the user making the request.
     * @param fileId The ID of the file to be deleted.
     * @throws NotFoundException if the file is not found.
     * @throws IllegalAccessException if the user tries to delete a file that doesn't belong to them.
     */
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

    /**
     * Retrieves the storage path for a file based on its filename.
     *
     * This method gets the file's physical path from the file storage system.
     *
     * @param fileName The name of the file whose storage path is to be retrieved.
     * @return A [Path] object representing the file's storage location.
     */
    override fun getFileStoragePath(fileName: String): Path {
        return fileStorageUtil.getFileStoragePath(fileName)
    }
}
