package me.sahil.book_management.file.controller


import jakarta.servlet.http.HttpServletResponse
import me.sahil.book_management.core.route.ApiRoutes
import me.sahil.book_management.core.utils.extractBearerToken
import me.sahil.book_management.file.dto.FileResponseDto
import me.sahil.book_management.file.service.FileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.FileInputStream


@RestController
@RequestMapping(ApiRoutes.FileRoutes.PATH)
class FileController(private val fileService: FileService) {

    // Endpoint to upload a file
    @PostMapping(ApiRoutes.FileRoutes.UPLOAD)
    fun uploadFile(
        @RequestHeader("Authorization") token: String,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Map<String, Any>> {
        val uploadedFile = fileService.uploadFile(token.extractBearerToken(), file)
        return ResponseEntity.ok(mapOf("message" to "File uploaded successfully", "file" to uploadedFile))
    }

    // Endpoint to get files uploaded by the current user
    @GetMapping(ApiRoutes.FileRoutes.USER_FILES)
    fun getUserFiles(@RequestHeader("Authorization") token: String): ResponseEntity<List<FileResponseDto>> {
        val files = fileService.getUserFiles(token.extractBearerToken())
        return ResponseEntity.ok(files)
    }

    // Endpoint to delete a file
    @DeleteMapping(ApiRoutes.FileRoutes.DELETE_FILE)
    fun deleteFile(@RequestHeader("Authorization") token: String, @PathVariable fileId: Long): ResponseEntity<String> {
        fileService.deleteFile(token.extractBearerToken(), fileId)
        return ResponseEntity.ok("File deleted successfully.")
    }

    // Endpoint to download a file
    @GetMapping(ApiRoutes.FileRoutes.DOWNLOAD_FILE)
    fun downloadFile(@PathVariable fileName: String, response: HttpServletResponse) {
        val file = fileService.getFileByUrl("http://localhost:8080/api/files/download/$fileName")  // Fetch file by URL

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
