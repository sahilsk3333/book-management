package me.sahil.book_management.file.service

import me.sahil.book_management.auth.dto.LoginRequest
import me.sahil.book_management.auth.dto.RegisterRequest
import me.sahil.book_management.auth.service.AuthServiceImpl
import me.sahil.book_management.core.role.Role
import me.sahil.book_management.file.repository.FileRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class FileServiceImplTest @Autowired constructor(
    private val fileService: FileServiceImpl,
    private val fileRepository: FileRepository,
    private val authService: AuthServiceImpl
) {
    private lateinit var userToken: String
    private var fileId: Long = 0

    @BeforeEach
    fun setup() {
        // Register a user
        val registerRequest = RegisterRequest(
            name = "Test User",
            email = "user@example.com",
            password = "password",
            role = Role.READER,
            age = 30
        )
        authService.register(registerRequest)

        // Login the user
        userToken = authService.login(
            LoginRequest(email = "user@example.com", password = "password")
        ).second

        // Mock a file upload
        val mockMultipartFile = MockMultipartFile(
            "file",
            "test-file.pdf",
            "application/pdf",
            "Sample content".toByteArray()
        )
        val fileResponse = fileService.uploadFile(userToken, "http://localhost:8080", mockMultipartFile)
        fileId = fileResponse.id
    }

    @Test
    fun `should upload a file and store metadata`() {
        val mockMultipartFile = MockMultipartFile(
            "file",
            "new-file.pdf",
            "application/pdf",
            "New file content".toByteArray()
        )
        val fileResponse = fileService.uploadFile(userToken, "http://localhost:8080", mockMultipartFile)

        assertTrue(fileResponse.fileName.endsWith("new-file.pdf"))
        assertTrue(fileRepository.existsById(fileResponse.id))
    }

    @Test
    fun `should retrieve all files for a user`() {
        val files = fileService.getUserFiles(userToken)

        assertEquals(1, files.size)
        assertTrue(files[0].fileName.endsWith("test-file.pdf"))
    }


    @Test
    fun `should mark a file as used`() {
        fileService.markFileAsUsed(fileId)

        val file = fileRepository.findById(fileId).orElseThrow()
        assertTrue(file.isUsed)
    }

    @Test
    fun `should delete a file belonging to the user`() {
        fileService.deleteFile(userToken, fileId)

        assertFalse(fileRepository.existsById(fileId))
    }

    @Test
    fun `should prevent deleting a file that doesn't belong to the user`() {
        // Register another user
        val registerRequest = RegisterRequest(
            name = "Another User",
            email = "another@example.com",
            password = "password",
            role = Role.READER,
            age = 25
        )
        authService.register(registerRequest)

        val anotherUserToken = authService.login(
            LoginRequest(email = "another@example.com", password = "password")
        ).second

        assertThrows<IllegalAccessException> {
            fileService.deleteFile(anotherUserToken, fileId)
        }
    }

    @Test
    fun `should clean up unused files`() {
        fileService.cleanUpUnusedFiles()

        assertFalse(fileRepository.existsById(fileId))
    }

    @Test
    fun `should retrieve a file by its URL`() {
        val file = fileRepository.findById(fileId).orElseThrow()
        val fileResponse = fileService.getFileByUrl(file.downloadUrl)

        assertNotNull(fileResponse)
        assertEquals(file.downloadUrl, fileResponse?.downloadUrl)
    }

    @Test
    fun `should return null for non-existent file URL`() {
        val fileResponse = fileService.getFileByUrl("http://localhost:8080/api/files/download/invalid-file")

        assertNull(fileResponse)
    }
}
