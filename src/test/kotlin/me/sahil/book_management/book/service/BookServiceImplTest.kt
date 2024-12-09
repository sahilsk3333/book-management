package me.sahil.book_management.book.service

import me.sahil.book_management.auth.dto.LoginRequest
import me.sahil.book_management.auth.dto.RegisterRequest
import me.sahil.book_management.auth.service.AuthServiceImpl
import me.sahil.book_management.book.dto.BookPartialUpdateRequest
import me.sahil.book_management.book.dto.BookRequest
import me.sahil.book_management.book.repository.BookRepository
import me.sahil.book_management.core.exception.NotFoundException
import me.sahil.book_management.core.role.Role
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional
import kotlin.properties.Delegates

@SpringBootTest
@Transactional
class BookServiceImplTest @Autowired constructor(
    private val bookService: BookServiceImpl,
    private val bookRepository: BookRepository,
    private val authService: AuthServiceImpl,
) {
    private lateinit var authorToken: String
    private lateinit var readerToken: String
    private var bookId by Delegates.notNull<Long>()

    @BeforeEach
    fun setup() {
        // Register an AUTHOR user
        val authorRegisterRequest = RegisterRequest(
            name = "Author User",
            email = "author@example.com",
            password = "authorpassword",
            role = Role.AUTHOR,
            age = 35
        )
        authService.register(authorRegisterRequest)

        authorToken = authService.login(
            LoginRequest(email = "author@example.com", password = "authorpassword")
        ).second

        // Register a READER user
        val readerRegisterRequest = RegisterRequest(
            name = "Reader User",
            email = "reader@example.com",
            password = "readerpassword",
            role = Role.READER,
            age = 25
        )
        authService.register(readerRegisterRequest)

        readerToken = authService.login(
            LoginRequest(email = "reader@example.com", password = "readerpassword")
        ).second

        // Add a sample book
        val bookRequest = BookRequest(
            name = "Sample Book",
            description = "A sample description",
            pdfUrl = "http://example.com/sample.pdf",
            isbn = "978-3-16-148410-0"
        )
        val bookResponse = bookService.addBook(authorToken, bookRequest)
        bookId = bookResponse.id
    }

    @Test
    fun `should list all books with pagination`() {
        val pageable: Pageable = PageRequest.of(0, 10)
        val books = bookService.getAllBooks(pageable)

        assertEquals(1, books.totalElements)
        assertEquals("Sample Book", books.content[0].name)
    }

    @Test
    fun `should allow author to add a book`() {
        val bookRequest = BookRequest(
            name = "New Book",
            description = "A new book description",
            pdfUrl = "http://example.com/new.pdf",
            isbn = "978-3-16-148410-1"
        )
        val bookResponse = bookService.addBook(authorToken, bookRequest)

        assertEquals("New Book", bookResponse.name)
        assertEquals("978-3-16-148410-1", bookResponse.isbn)
    }

    @Test
    fun `should prevent READER from adding a book`() {
        val bookRequest = BookRequest(
            name = "Unauthorized Book",
            description = "Reader shouldn't add this",
            pdfUrl = "http://example.com/unauthorized.pdf",
            isbn = "978-3-16-148410-2"
        )

        assertThrows<IllegalAccessException> {
            bookService.addBook(readerToken, bookRequest)
        }
    }

    @Test
    fun `should allow author to delete their own book`() {
        bookService.deleteBook(authorToken, bookId)
        assertFalse(bookRepository.existsById(bookId))
    }

    @Test
    fun `should prevent READER from deleting any book`() {
        assertThrows<IllegalAccessException> {
            bookService.deleteBook(readerToken, bookId)
        }
    }

    @Test
    fun `should update book details by the author`() {
        val updateRequest = BookRequest(
            name = "Updated Book Name",
            description = "Updated description",
            pdfUrl = "http://example.com/updated.pdf",
            isbn = "978-3-16-148410-0"
        )
        val updatedBook = bookService.updateBook(authorToken, bookId, updateRequest)

        assertEquals("Updated Book Name", updatedBook.name)
        assertEquals("Updated description", updatedBook.description)
    }

    @Test
    fun `should prevent author from updating another author's book`() {
        // Register another author and their book
        val anotherAuthorRequest = RegisterRequest(
            name = "Another Author",
            email = "another.author@example.com",
            password = "password",
            role = Role.AUTHOR,
            age = 40
        )
        authService.register(anotherAuthorRequest)

        val anotherAuthorToken = authService.login(
            LoginRequest(email = "another.author@example.com", password = "password")
        ).second

        val anotherBookRequest = BookRequest(
            name = "Another Author Book",
            description = "Another author's book",
            pdfUrl = "http://example.com/another.pdf",
            isbn = "978-3-16-148410-3"
        )
        val anotherBookResponse = bookService.addBook(anotherAuthorToken, anotherBookRequest)

        val updateRequest = BookRequest(
            name = "Unauthorized Update",
            description = "Trying to update another author's book",
            pdfUrl = "http://example.com/unauthorized-update.pdf",
            isbn = "978-3-16-148410-3"
        )

        assertThrows<IllegalAccessException> {
            bookService.updateBook(authorToken, anotherBookResponse.id, updateRequest)
        }
    }

    @Test
    fun `should allow partial update of a book`() {
        val partialUpdateRequest = BookPartialUpdateRequest(
            name = "Partially Updated Name",
        )

        val updatedBook = bookService.partialUpdateBook(authorToken, bookId, partialUpdateRequest)

        assertEquals("Partially Updated Name", updatedBook.name)
        assertEquals("A sample description", updatedBook.description)
    }

    @Test
    fun `should retrieve a book by ID`() {
        val bookResponse = bookService.getBookById(bookId)

        assertEquals("Sample Book", bookResponse.name)
        assertEquals("978-3-16-148410-0", bookResponse.isbn)
    }

    @Test
    fun `should throw exception when retrieving a non-existent book`() {
        assertThrows<NotFoundException> {
            bookService.getBookById(999L)
        }
    }
}
