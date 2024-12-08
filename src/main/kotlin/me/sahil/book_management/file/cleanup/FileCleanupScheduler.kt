package me.sahil.book_management.file.cleanup

import me.sahil.book_management.file.service.FileService
import me.sahil.book_management.file.service.FileServiceImpl
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class FileCleanupScheduler(private val fileService: FileService) {

    @Scheduled(cron = "0 0 0 * * ?")  // Runs every day at midnight
    fun cleanUpUnusedFiles() {
        fileService.cleanUpUnusedFiles()
    }
}
