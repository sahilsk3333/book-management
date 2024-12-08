package me.sahil.book_management.file.util

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

/**
 * Utility class for handling file storage operations.
 *
 * This class provides methods for storing, deleting, and retrieving files in a local file storage system.
 * The files are stored in a directory named "uploads" that is created during initialization if it does not exist.
 */
@Component
class FileStorageUtil {

    // The path to the directory where uploaded files will be stored
    private val uploadDirectory: Path = Paths.get("uploads").toAbsolutePath().normalize()

    init {
        // Ensure the upload directory exists
        Files.createDirectories(uploadDirectory)
    }

    /**
     * Stores the uploaded file in the file storage system.
     *
     * This method generates a unique filename for the file, stores it in the "uploads" directory,
     * and returns the generated filename.
     *
     * @param file The file to be uploaded and stored.
     * @return The generated filename of the stored file.
     * @throws IOException if an I/O error occurs while storing the file.
     */
    fun storeFile(file: MultipartFile): String {
        // Generate a unique file name by combining a UUID with the original file name
        val fileName = "${UUID.randomUUID()}-${file.originalFilename}"
        val targetLocation = uploadDirectory.resolve(fileName)
        file.inputStream.use { inputStream ->
            // Copy the file input stream to the target location in the storage directory
            Files.copy(inputStream, targetLocation)
        }
        return fileName
    }

    /**
     * Deletes a file from the file storage system.
     *
     * This method deletes the file identified by the given filename from the "uploads" directory.
     * If the file does not exist, no action is taken.
     *
     * @param fileName The name of the file to be deleted.
     * @throws IOException if an I/O error occurs while deleting the file.
     */
    fun deleteFile(fileName: String) {
        val targetLocation = uploadDirectory.resolve(fileName)
        Files.deleteIfExists(targetLocation)  // Delete the file if it exists
    }

    /**
     * Retrieves the path of the stored file.
     *
     * This method returns the full path to the file stored in the "uploads" directory based on its filename.
     *
     * @param fileName The name of the file whose path is to be retrieved.
     * @return The path of the file in the file storage system.
     */
    fun getFileStoragePath(fileName: String): Path {
        return uploadDirectory.resolve(fileName)  // Resolve the file path in the upload directory
    }
}
