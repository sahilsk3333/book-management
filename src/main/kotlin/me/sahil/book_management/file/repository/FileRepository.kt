package me.sahil.book_management.file.repository

import me.sahil.book_management.file.Entity.File
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for managing `File` entities.
 *
 * This interface extends [JpaRepository] and provides CRUD operations for the `File` entity.
 * It includes custom query methods to find files based on specific criteria, such as:
 * - Files uploaded by a specific user
 * - Files with a specific download URL
 * - Unused files that need periodic cleanup.
 */
@Repository
interface FileRepository : JpaRepository<File, Long> {

    /**
     * Find all files uploaded by a specific user.
     *
     * This method retrieves all `File` entities that belong to a user specified by their user ID.
     *
     * @param userId The ID of the user whose files are to be retrieved.
     * @return A list of `File` entities uploaded by the user.
     */
    fun findByUserId(userId: Long): List<File>

    /**
     * Find a file by its download URL.
     *
     * This method retrieves a `File` entity based on its download URL.
     *
     * @param downloadUrl The download URL of the file to retrieve.
     * @return The `File` entity associated with the given download URL, or `null` if no such file exists.
     */
    fun findByDownloadUrl(downloadUrl: String): File?

    /**
     * Find unused files that need periodic cleanup.
     *
     * This method retrieves all `File` entities that are marked as unused (i.e., `isUsed` is `false`).
     * These files can be candidates for cleanup or deletion.
     *
     * @return A list of `File` entities that are unused.
     */
    fun findByIsUsedFalse(): List<File>
}
