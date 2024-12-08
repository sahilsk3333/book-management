package me.sahil.book_management.file.repository

import me.sahil.book_management.file.Entity.File
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository : JpaRepository<File, Long> {

    // Find all files uploaded by a specific user
    fun findByUserId(userId: Long): List<File>

    // Find a file by its download URL
    fun findByDownloadUrl(downloadUrl: String): File?

    // Find unused files that need periodic cleanup
    fun findByIsUsedFalse(): List<File>
}