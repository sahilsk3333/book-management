package me.sahil.book_management.file.service

import me.sahil.book_management.file.dto.FileResponseDto
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

/**
 * Service interface for handling file-related operations.
 *
 * This interface defines methods to manage file uploads, deletions, retrieval, and cleanup.
 * It also includes methods for marking files as used and getting files by their download URL.
 */
interface FileService {

    /**
     * Uploads a new file and stores it in the appropriate location.
     *
     * This method processes the uploaded file, associates it with the user identified by the provided
     * authorization token, and generates a URL for downloading the file. The file is stored on the server
     * and can be retrieved using the generated download URL.
     *
     * @param token The authorization token used to authenticate the user uploading the file.
     * @param serverBaseUrl The base URL of the server, used to generate the file's download URL.
     * @param file The file to be uploaded.
     * @return A [FileResponseDto] containing the details of the uploaded file, including the file name,
     *         MIME type, and the generated download URL.
     * @throws TokenInvalidException If the provided token is invalid or missing.
     */
    fun uploadFile(token: String, serverBaseUrl: String, file: MultipartFile): FileResponseDto


    /**
     * Mark a file as used.
     *
     * This method updates the status of the file to indicate that it has been used.
     *
     * @param fileId The ID of the file to be marked as used.
     */
    fun markFileAsUsed(fileId: Long)

    /**
     * Retrieve all files uploaded by the current user.
     *
     * This method returns a list of files associated with the user identified by the provided token.
     *
     * @param token The authorization token of the user.
     * @return A list of [FileResponseDto] objects representing the files uploaded by the user.
     * @throws TokenInvalidException if the token is invalid or missing.
     */
    fun getUserFiles(token: String): List<FileResponseDto>

    /**
     * Retrieve a file by its download URL.
     *
     * This method fetches the details of a file based on its download URL.
     *
     * @param downloadUrl The download URL of the file.
     * @return A [FileResponseDto] containing the file's details, or `null` if the file does not exist.
     */
    fun getFileByUrl(downloadUrl: String): FileResponseDto?

    /**
     * Delete a file.
     *
     * This method deletes the file identified by its ID.
     *
     * @param token The authorization token of the user deleting the file.
     * @param fileId The ID of the file to be deleted.
     * @throws TokenInvalidException if the token is invalid or missing.
     * @throws NotFoundException if the file is not found.
     */
    fun deleteFile(token: String, fileId: Long)

    /**
     * Get the storage path for a file based on its filename.
     *
     * This method retrieves the file storage path for the file identified by its filename.
     *
     * @param fileName The name of the file whose storage path is to be retrieved.
     * @return The [Path] object representing the file's storage path.
     */
    fun getFileStoragePath(fileName: String): Path

    /**
     * Perform periodic cleanup of unused files.
     *
     * This method removes files that are marked as unused (`isUsed = false`).
     * It can be scheduled to run at regular intervals to free up storage space.
     */
    fun cleanUpUnusedFiles()
}
