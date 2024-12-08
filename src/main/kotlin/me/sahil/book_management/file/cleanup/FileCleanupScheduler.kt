package me.sahil.book_management.file.cleanup

import me.sahil.book_management.file.service.FileService
import me.sahil.book_management.file.service.FileServiceImpl
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * A scheduled task component that cleans up unused files in the system.
 *
 * This class runs a file cleanup process at midnight every day. It is responsible for invoking the
 * [FileService.cleanUpUnusedFiles] method to remove any unused files that are no longer needed.
 * The cleanup process is scheduled using Spring's `@Scheduled` annotation with a cron expression
 * set to run every day at midnight.
 *
 * @param fileService The service responsible for cleaning up unused files.
 */
@Component
class FileCleanupScheduler(private val fileService: FileService) {

    /**
     * Scheduled task that runs every day at midnight to clean up unused files.
     *
     * This method calls the [FileService.cleanUpUnusedFiles] method to perform the cleanup. It is triggered
     * automatically by the scheduler according to the cron expression defined in the `@Scheduled` annotation.
     */
    @Scheduled(cron = "0 0 0 * * ?")  // Runs every day at midnight
    fun cleanUpUnusedFiles() {
        fileService.cleanUpUnusedFiles()
    }
}

