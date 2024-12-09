package me.sahil.book_management.user.service

import me.sahil.book_management.auth.dto.LoginRequest
import me.sahil.book_management.auth.dto.RegisterRequest
import me.sahil.book_management.auth.security.JwtTokenProvider
import me.sahil.book_management.auth.service.AuthServiceImpl
import me.sahil.book_management.core.exception.NotFoundException
import me.sahil.book_management.core.role.Role
import me.sahil.book_management.user.dto.PartialUpdateUserRequest
import me.sahil.book_management.user.dto.UpdateUserRequest
import me.sahil.book_management.user.entity.User
import me.sahil.book_management.user.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@Transactional
class UserServiceImplTest @Autowired constructor(
    private val userService: UserServiceImpl,
    private val userRepository: UserRepository,
    private val authService: AuthServiceImpl,
    private val jwtTokenProvider: JwtTokenProvider
) {

    private lateinit var adminToken: String
    private lateinit var authorToken: String
    private lateinit var readerToken: String
    private var userId: Long = 0L

    @BeforeEach
    fun setup() {
        // Register an ADMIN user
        val adminRequest = RegisterRequest(
            name = "Admin User",
            email = "admin@example.com",
            password = "adminpassword",
            role = Role.ADMIN,
            age = 40
        )
        authService.register(adminRequest)
        adminToken = authService.login(LoginRequest("admin@example.com", "adminpassword")).second

        // Register an AUTHOR user
        val authorRequest = RegisterRequest(
            name = "Author User",
            email = "author@example.com",
            password = "authorpassword",
            role = Role.AUTHOR,
            age = 30
        )
        authService.register(authorRequest)
        authorToken = authService.login(LoginRequest("author@example.com", "authorpassword")).second

        // Register a READER user
        val readerRequest = RegisterRequest(
            name = "Reader User",
            email = "reader@example.com",
            password = "readerpassword",
            role = Role.READER,
            age = 25
        )
        authService.register(readerRequest)
        readerToken = authService.login(LoginRequest("reader@example.com", "readerpassword")).second

        // Create a user to be used in the tests
        val user = userRepository.save(
            User(
                name = "Test User",
                email = "testuser@example.com",
                password = "password",
                role = Role.AUTHOR,
                age = 30
            )
        )
        userId = user.id
    }

    @Test
    fun `should get all users when admin requests`() {
        val pageable: Pageable = PageRequest.of(0, 10)

        val users = userService.getAllUsers(adminToken, pageable)

        assertEquals(3, users.totalElements)
    }

    @Test
    fun `should throw exception when non-admin tries to get all users`() {
        assertThrows<IllegalAccessException> {
            userService.getAllUsers(readerToken, PageRequest.of(0, 10))
        }
    }

    @Test
    fun `should update user details`() {
        val updateRequest = UpdateUserRequest(
            name = "Updated User",
            email = "updated@example.com",
            age = 35,
            image = null,
            role = Role.AUTHOR
        )

        val updatedUser = userService.updateUser(authorToken, updateRequest)

        assertEquals("Updated User", updatedUser.name)
        assertEquals("updated@example.com", updatedUser.email)
    }


    @Test
    fun `should update user details partially`() {
        val partialUpdateRequest = PartialUpdateUserRequest(name = "Partially Updated User")

        val updatedUser = userService.updateUser(authorToken,partialUpdateRequest)

        assertEquals("Partially Updated User", updatedUser.name)
    }


    @Test
    fun `should delete user when admin requests`() {
        userService.deleteUser(adminToken, userId)

        assertThrows<NotFoundException> {
            userRepository.findById(userId).orElseThrow { NotFoundException("User not found") }
        }
    }

    @Test
    fun `should throw exception when non-admin tries to delete user`() {
        assertThrows<IllegalAccessException> {
            userService.deleteUser(readerToken, userId)
        }
    }


    @Test
    fun `should get user by ID when the user is requesting their own profile`() {
        val claims = jwtTokenProvider.getUserDetailsFromToken(authorToken)
        val user = userService.getUserById(authorToken, claims.id)

        assertEquals("Author User", user.name)
        assertEquals("author@example.com", user.email)
    }

    @Test
    fun `should get user by ID when the admin is requesting`() {
        val user = userService.getUserById(adminToken, userId)
        assertEquals("Test User", user.name)
        assertEquals("testuser@example.com", user.email)
    }


    @Test
    fun `should throw exception when user tries to get another user's profile`() {
        assertThrows<IllegalAccessException> {
            userService.getUserById(readerToken, userId)
        }
    }

    @Test
    fun `should get user by token`() {
        val user = userService.getUserByToken(authorToken)

        assertNotNull(user)
        assertEquals("Author User", user.name)
    }

}
