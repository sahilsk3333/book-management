package me.sahil.book_management.file.controller


import jakarta.servlet.http.HttpServletResponse
import me.sahil.book_management.core.route.ApiRoutes
import me.sahil.book_management.core.utils.extractBearerToken
import me.sahil.book_management.file.dto.FileResponseDto
import me.sahil.book_management.file.service.FileService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.FileInputStream


/**
 * Controller responsible for handling file-related operations.
 *
 * This class provides endpoints for uploading, retrieving, deleting, and downloading files.
 * It interacts with the [FileService] to perform the file management operations.
 * All endpoints require an authenticated user, and authorization is handled via the `Authorization` header.
 *
 * @param fileService The service responsible for performing file operations.
 */
@RestController
@RequestMapping(ApiRoutes.FileRoutes.PATH)
class FileController(private val fileService: FileService) {

    @Value("\${server.base-url}")
    lateinit var serverBaseUrl: String

    /**
     * Endpoint to upload a file.
     *
     * This endpoint allows an authenticated user to upload a file. The file is passed as a
     * multipart request parameter, and the Authorization token is provided via the `Authorization`
     * header. The file is processed and uploaded by the [fileService.uploadFile] method.
     *
     * @param token The Authorization token for the user making the request.
     * @param file The file to be uploaded.
     * @return A `ResponseEntity` containing a success message and the uploaded file's details.
     */
    @PostMapping(ApiRoutes.FileRoutes.UPLOAD)
    fun uploadFile(
        @RequestHeader("Authorization") token: String,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Map<String, Any>> {
        val uploadedFile = fileService.uploadFile(token.extractBearerToken(), serverBaseUrl = serverBaseUrl, file)
        return ResponseEntity.ok(mapOf("message" to "File uploaded successfully", "file" to uploadedFile))
    }

    /**
     * Endpoint to retrieve files uploaded by the current user.
     *
     * This endpoint allows an authenticated user to get a list of files they have uploaded.
     * The Authorization token is passed via the `Authorization` header.
     *
     * @param token The Authorization token for the user making the request.
     * @return A `ResponseEntity` containing a list of files uploaded by the user.
     */
    @GetMapping(ApiRoutes.FileRoutes.USER_FILES)
    fun getUserFiles(@RequestHeader("Authorization") token: String): ResponseEntity<List<FileResponseDto>> {
        val files = fileService.getUserFiles(token.extractBearerToken())
        return ResponseEntity.ok(files)
    }

    /**
     * Endpoint to delete a specific file.
     *
     * This endpoint allows an authenticated user to delete a file they have uploaded.
     * The Authorization token is passed via the `Authorization` header, and the `fileId`
     * is provided as a path variable.
     *
     * @param token The Authorization token for the user making the request.
     * @param fileId The ID of the file to be deleted.
     * @return A `ResponseEntity` with a message indicating the file was deleted successfully.
     */
    @DeleteMapping(ApiRoutes.FileRoutes.DELETE_FILE)
    fun deleteFile(@RequestHeader("Authorization") token: String, @PathVariable fileId: Long): ResponseEntity<String> {
        fileService.deleteFile(token.extractBearerToken(), fileId)
        return ResponseEntity.ok("File deleted successfully.")
    }

    /**
     * Endpoint to download a specific file.
     *
     * This endpoint allows an authenticated user to download a file. The file name is passed
     * as a path variable. The file is fetched and streamed to the client via the HTTP response.
     *
     * @param fileName The name of the file to be downloaded.
     * @param response The `HttpServletResponse` to stream the file data to the client.
     */
    @GetMapping(ApiRoutes.FileRoutes.DOWNLOAD_FILE)
    fun downloadFile(@PathVariable fileName: String, response: HttpServletResponse) {
        val file = fileService.getFileByUrl("$serverBaseUrl/api/files/download/$fileName")  // Fetch file by URL

        if (file == null) {
            response.status = HttpServletResponse.SC_NOT_FOUND
            response.writer.write("File not found")
            return
        }

        // Get file storage path and stream the file
        val targetFile = fileService.getFileStoragePath(file.fileName)
        val inputStream = FileInputStream(targetFile.toFile())

        // Set response headers for file download
        response.apply {
            contentType = file.mimeType
            setHeader("Content-Disposition", "attachment; filename=${file.fileName}")
            setContentLength(targetFile.toFile().length().toInt())
        }

        // Stream the file to the client
        inputStream.use {
            it.copyTo(response.outputStream)
        }
    }
}

