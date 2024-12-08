package me.sahil.book_management.file

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@Component
class FileStorageUtil {

    private val uploadDirectory: Path = Paths.get("uploads").toAbsolutePath().normalize()

    init {
        Files.createDirectories(uploadDirectory)  // Ensure the upload directory exists
    }

    // Store the uploaded file
    fun storeFile(file: MultipartFile): String {
        val fileName = "${UUID.randomUUID()}-${file.originalFilename}"
        val targetLocation = uploadDirectory.resolve(fileName)
        file.inputStream.use { inputStream ->
            Files.copy(inputStream, targetLocation)
        }
        return fileName
    }

    // Delete the physical file from the storage
    fun deleteFile(fileName: String) {
        val targetLocation = uploadDirectory.resolve(fileName)
        Files.deleteIfExists(targetLocation)
    }

    // Get the file storage path
    fun getFileStoragePath(fileName: String): Path {
        return uploadDirectory.resolve(fileName)
    }
}
