package me.sahil.book_management.book.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.sahil.book_management.auth.security.JwtTokenProvider
import me.sahil.book_management.book.dto.BookPartialUpdateRequest
import me.sahil.book_management.book.dto.BookRequest
import me.sahil.book_management.book.dto.BookResponse
import me.sahil.book_management.book.service.BookService
import me.sahil.book_management.core.role.Role
import me.sahil.book_management.user.dto.UserResponse
import me.sahil.book_management.user.entity.User
import org.junit.jupiter.api.BeforeEach
import org.mockito.ArgumentMatchers.eq
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
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
class BookControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    @MockitoBean private val bookService: BookService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper
) {

    lateinit var token: String

    @BeforeEach
    fun setUp() {
        token = jwtTokenProvider.generateToken(
            User().copy(
                id = 1L,
                email = "test@example.com",
                name = "Test Author",
                role = Role.AUTHOR
            )
        )
    }

    @Test
    fun `should return paginated list of books`() {
        val books = listOf(
            BookResponse(
                id = 1L,
                author = UserResponse(
                    id = 1L,
                    name = "Test Author",
                    email = "test@example.com",
                    age = null,
                    role = Role.AUTHOR,
                    image = null
                ),
                name = "Test Book",
                description = "A test book description",
                pdfUrl = "http://example.com/test.pdf",
                isbn = "123-456-789",
                createdAt = ZonedDateTime.now()
            ),
        )
        val bookPage = PageImpl(books)

        whenever(bookService.getAllBooks(PageRequest.of(0, 20))).thenReturn(bookPage)


        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/books")
                .header("Authorization", "Bearer $token")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Test Book"))
    }

    @Test
    fun `should add a new book`() {
        val bookRequest = BookRequest(
            name = "New Book",
            description = "A new book description",
            pdfUrl = "http://example.com/new-book.pdf",
            isbn = "978-3-16-108410-4",
        )
        val addedBook = BookResponse(
            id = 2L,
            author = UserResponse(
                id = 1L,
                name = "Test Author",
                email = "test@example.com",
                age = null,
                role = Role.AUTHOR,
                image = null
            ),
            name = "New Book",
            description = "A new book description",
            pdfUrl = "http://example.com/new-book.pdf",
            isbn = "978-3-16-108410-4",
            createdAt = ZonedDateTime.now()
        )

        whenever(bookService.addBook(any(), any())).thenReturn(addedBook)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/books")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest))
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("New Book"))
    }

    @Test
    fun `should return validation error for invalid ISBN`() {
        // Create a BookRequest with an invalid ISBN format
        val invalidBookRequest = BookRequest(
            name = "New Book",
            description = "A new book description",
            pdfUrl = "http://example.com/new-book.pdf",
            isbn = "invalid-isbn"  // Invalid ISBN
        )

        // Perform the POST request with the invalid ISBN
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/books")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBookRequest))
        )
            // Expect a 400 Bad Request status
            .andExpect(status().isBadRequest)
            // Check if the response body contains the appropriate error message
            .andExpect(jsonPath("$.message").value("isbn: Invalid ISBN format, must be either ISBN-10 or ISBN-13"))
    }

    @Test
    fun `should update a book`() {
        val bookRequest = BookRequest(
            name = "Updated Book",
            description = "Updated book description",
            pdfUrl = "http://example.com/updated-book.pdf",
            isbn = "978-3-16-108410-4",
        )
        val updatedBook = BookResponse(
            id = 1L,
            author = UserResponse(
                id = 1L,
                name = "Test Author",
                email = "test@example.com",
                age = null,
                role = Role.AUTHOR,
                image = null
            ),
            name = "Updated Book",
            description = "Updated book description",
            pdfUrl = "http://example.com/updated-book.pdf",
            isbn = "978-3-16-108410-4",
            createdAt = ZonedDateTime.now()
        )

        whenever(bookService.updateBook(any(), any(), any())).thenReturn(updatedBook)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/books/{bookId}", 1L)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(bookRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Updated Book"))
    }


    @Test
    fun `should partially update a book`() {
        val bookPartialUpdateRequest = BookPartialUpdateRequest(
            description = "Updated description"
        )
        val updatedBook = BookResponse(
            id = 1L,
            author = UserResponse(
                id = 1L,
                name = "Test Author",
                email = "test@example.com",
                age = null,
                role = Role.AUTHOR,
                image = null
            ),
            name = "Test Book",
            description = "Updated description",
            pdfUrl = "http://example.com/test.pdf",
            isbn = "978-3-16-108410-4",
            createdAt = ZonedDateTime.now()
        )

        whenever(bookService.partialUpdateBook(any(), any(), any())).thenReturn(updatedBook)

        mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/books/{bookId}", 1L)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ObjectMapper().writeValueAsString(bookPartialUpdateRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.description").value("Updated description"))
    }

    @Test
    fun `should delete a book and return a success message`() {
        val bookId = 1L  // The ID of the book to be deleted

        // Mock the deleteBook method to do nothing (since it returns void)
        doNothing().`when`(bookService).deleteBook(any(), eq(bookId))

        // Perform the DELETE request
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/books/$bookId")
                .header("Authorization", "Bearer $token")
                .accept(MediaType.APPLICATION_JSON)
        )
            // Expect status 200 OK
            .andExpect(status().isOk)
            // Check that the response body contains the success message
            .andExpect(jsonPath("$.message").value("Book with ID $bookId has been deleted."))
    }


    @Test
    fun `should retrieve a specific book by its ID`() {
        val bookId = 1L  // The ID of the book to retrieve
        val bookResponse = BookResponse(
            id = bookId,
            author = UserResponse(
                id = 1L,
                name = "Test Author",
                email = "test@example.com",
                age = null,
                role = Role.AUTHOR,
                image = null
            ),
            name = "Test Book",
            description = "A test book description",
            pdfUrl = "http://example.com/test-book.pdf",
            isbn = "123-456-789",
            createdAt = ZonedDateTime.now()
        )

        // Mock the getBookById method to return the bookResponse
        whenever(bookService.getBookById(bookId)).thenReturn(bookResponse)

        // Perform the GET request to retrieve the book
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/books/$bookId")
                .header("Authorization", "Bearer $token")
                .accept(MediaType.APPLICATION_JSON)
        )
            // Expect status 200 OK
            .andExpect(status().isOk)
            // Check that the response body contains the correct book details
            .andExpect(jsonPath("$.id").value(bookId))
            .andExpect(jsonPath("$.name").value("Test Book"))
            .andExpect(jsonPath("$.author.name").value("Test Author"))
            .andExpect(jsonPath("$.isbn").value("123-456-789"))
    }

}