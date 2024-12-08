package me.sahil.book_management.file.controller


import jakarta.servlet.http.HttpServletResponse
import me.sahil.book_management.file.dto.FileResponseDto
import me.sahil.book_management.file.service.FileService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.FileInputStream


@RestController
@RequestMapping("/api/files")
class FileController(private val fileService: FileService) {

    // Endpoint to upload a file
    @PostMapping("/upload")
    fun uploadFile(@RequestHeader("Authorization") token: String, @RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        val uploadedFile = fileService.uploadFile(token.removePrefix("Bearer "), file)
        return ResponseEntity.ok("File uploaded successfully. Download URL: ${uploadedFile.downloadUrl}")
    }

    // Endpoint to get files uploaded by the current user
    @GetMapping("/user-files")
    fun getUserFiles(@RequestHeader("Authorization") token: String): ResponseEntity<List<FileResponseDto>> {
        val files = fileService.getUserFiles(token.removePrefix("Bearer "))
        return ResponseEntity.ok(files)
    }

    // Endpoint to delete a file
    @DeleteMapping("/{fileId}")
    fun deleteFile(@RequestHeader("Authorization") token: String, @PathVariable fileId: Long): ResponseEntity<String> {
        fileService.deleteFile(token.removePrefix("Bearer "), fileId)
        return ResponseEntity.ok("File deleted successfully.")
    }

    // Endpoint to download a file
    @GetMapping("/download/{fileName}")
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
        response.contentType = file.mimeType
        response.setHeader("Content-Disposition", "attachment; filename=${file.fileName}")
        response.setContentLength(targetFile.toFile().length().toInt())

        // Stream the file to the client
        inputStream.copyTo(response.outputStream)
        inputStream.close()
    }
}
