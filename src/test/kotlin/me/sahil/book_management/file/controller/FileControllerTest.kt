package me.sahil.book_management.file.controller


import me.sahil.book_management.auth.security.JwtTokenProvider
import me.sahil.book_management.core.role.Role
import me.sahil.book_management.file.dto.FileResponseDto
import me.sahil.book_management.file.service.FileService
import me.sahil.book_management.user.entity.User
import org.junit.jupiter.api.BeforeEach
import org.mockito.ArgumentMatchers.eq
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.ZonedDateTime
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FileControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    @MockitoBean private val fileService: FileService,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    lateinit var token: String

    @BeforeEach
    fun setUp() {
        // Generate a JWT token for authentication
        token = jwtTokenProvider.generateToken(
            User().copy(
                id = 1L,
                email = "test@example.com",
                name = "Test User",
                role = Role.READER
            )
        )
    }

    @Test
    fun `should upload a file and return success message`() {
        val file = MockMultipartFile("file", "test-file.txt", "text/plain", "Test file content".toByteArray())

        val uploadedFileResponse = FileResponseDto(
            id = 1L,
            fileName = "test-file.txt",
            mimeType = "text/plain",
            downloadUrl = "/api/files/download/test-file.txt",
            createdAt = ZonedDateTime.now()
        )

        // Mocking the uploadFile method to return the uploadedFileResponse
        whenever(fileService.uploadFile(any(), any(), any())).thenReturn(uploadedFileResponse)

        mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/files/upload")
                .file(file)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("File uploaded successfully"))
            .andExpect(jsonPath("$.file.fileName").value("test-file.txt"))
            .andExpect(jsonPath("$.file.downloadUrl").value("/api/files/download/test-file.txt"))
    }

    @Test
    fun `should return a list of files uploaded by the user`() {
        val fileResponse = FileResponseDto(
            id = 1L,
            fileName = "test-file.txt",
            mimeType = "text/plain",
            downloadUrl = "/api/files/download/test-file.txt",
            createdAt = ZonedDateTime.now()
        )
        val files = listOf(fileResponse)

        // Mocking the getUserFiles method to return the files list
        whenever(fileService.getUserFiles(any())).thenReturn(files)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/files/user-files")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].fileName").value("test-file.txt"))
            .andExpect(jsonPath("$[0].downloadUrl").value("/api/files/download/test-file.txt"))
    }

    @Test
    fun `should delete a file and return success message`() {
        val fileId = 1L

        // Mocking the deleteFile method to do nothing
        doNothing().whenever(fileService).deleteFile(any(), eq(fileId))

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/files/$fileId")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").value("File deleted successfully."))
    }


}
