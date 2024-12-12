package me.sahil.book_management.user.controller


import com.fasterxml.jackson.databind.ObjectMapper
import me.sahil.book_management.auth.security.JwtTokenProvider
import me.sahil.book_management.core.role.Role
import me.sahil.book_management.core.route.ApiRoutes
import me.sahil.book_management.user.dto.PartialUpdateUserRequest
import me.sahil.book_management.user.dto.UpdateUserRequest
import me.sahil.book_management.user.dto.UserResponse
import me.sahil.book_management.user.entity.User
import me.sahil.book_management.user.service.UserService
import org.junit.jupiter.api.BeforeEach
import org.mockito.ArgumentMatchers.eq
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    @MockitoBean private val userService: UserService,
    private val jwtTokenProvider: JwtTokenProvider,
    private val objectMapper: ObjectMapper
) {

    lateinit var token: String

    @BeforeEach
    fun setUp() {
        // Set up a valid JWT token
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
    fun `should get all users`() {
        // Mock the user service to return a paginated list of users
        val users = PageImpl(
            listOf(UserResponse(1L, "Test User", "test@example.com", 30, "image.jpg", Role.READER)),
            Pageable.unpaged(),
            1
        )

        whenever(userService.getAllUsers(any(), any())).thenReturn(users)

        mockMvc.perform(
            MockMvcRequestBuilders.get(ApiRoutes.UserRoutes.PATH + ApiRoutes.UserRoutes.GET_ALL_USERS)
                .header("Authorization", "Bearer $token")
                .param("page", "0")
                .param("size", "10")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].name").value("Test User"))
            .andExpect(jsonPath("$.content[0].email").value("test@example.com"))
    }

    @Test
    fun `should update user details`() {
        val updateUserRequest = UpdateUserRequest(
            name = "Updated User",
            email = "updated@example.com",
            role = Role.READER,
            image = "image.jpg",
            age = 21
        )
        val updatedUser = UserResponse(
            id = 1L, // Mocked ID
            name = updateUserRequest.name,
            email = updateUserRequest.email,
            age = updateUserRequest.age,
            image = updateUserRequest.image,
            role = updateUserRequest.role
        )

        // Mock the userService to return the updated user response
        whenever(userService.updateUser(any(), any())).thenReturn(updatedUser)

        mockMvc.perform(
            MockMvcRequestBuilders.put(ApiRoutes.UserRoutes.PATH + ApiRoutes.UserRoutes.UPDATE_USER)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(updateUserRequest.name))
            .andExpect(jsonPath("$.email").value(updateUserRequest.email))
            .andExpect(jsonPath("$.age").value(updateUserRequest.age))
    }


    @Test
    fun `should partially update user details`() {
        val partialUpdateRequest = PartialUpdateUserRequest(name = "Partially Updated User")
        val updatedUser = UserResponse(
            id = 1L, name = "Partially Updated User",
            email = "test@example.com",
            age = 30,
            image = "image.jpg",
            Role.READER
        )

        // Mocking the userService to return updated user details
        whenever(userService.patchUser(any(), any())).thenReturn(updatedUser)

        mockMvc.perform(
            MockMvcRequestBuilders.patch(ApiRoutes.UserRoutes.PATH + ApiRoutes.UserRoutes.PATCH_USER)
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialUpdateRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value(partialUpdateRequest.name))
    }

    @Test
    fun `should delete a user`() {
        val userId = 1L

        // Mocking the userService to delete a user
        doNothing().whenever(userService).deleteUser(any(), eq(userId))

        mockMvc.perform(
            MockMvcRequestBuilders.delete("${ApiRoutes.UserRoutes.PATH}/$userId")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("User with ID $userId has been deleted successfully."))
    }

    @Test
    fun `should get user by ID`() {
        val userId = 1L
        val user = UserResponse(userId, "Test User", "test@example.com", 30, "image.jpg", Role.READER)

        // Mocking the userService to return the user by ID
        whenever(userService.getUserById(any(), eq(userId))).thenReturn(user)

        mockMvc.perform(
            MockMvcRequestBuilders.get("${ApiRoutes.UserRoutes.PATH}/$userId")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
    }

    @Test
    fun `should get authenticated user's profile`() {
        val user = UserResponse(1L, "Test User", "test@example.com", 30, "image.jpg", Role.READER)

        // Mocking the userService to return the authenticated user's profile
        whenever(userService.getUserByToken(any())).thenReturn(user)

        mockMvc.perform(
            MockMvcRequestBuilders.get(ApiRoutes.UserRoutes.PATH + ApiRoutes.UserRoutes.PROFILE)
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
    }
}
