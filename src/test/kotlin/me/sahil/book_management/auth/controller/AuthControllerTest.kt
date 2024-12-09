package me.sahil.book_management.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.sahil.book_management.auth.dto.LoginRequest
import me.sahil.book_management.auth.dto.RegisterRequest
import me.sahil.book_management.auth.service.AuthService
import me.sahil.book_management.core.role.Role
import me.sahil.book_management.user.dto.UserResponse
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockitoBean
    lateinit var authService: AuthService

    // Test case for user registration
    @Test
    fun `should register user successfully`() {
        val registerRequest = RegisterRequest(
            name = "John Doe",
            email = "john.doe@example.com",
            password = "password123",
            role = Role.READER,
            age = 30,
        )

        val userResponse = UserResponse(
            id = 1L,
            name = "John Doe",
            email = "john.doe@example.com",
            age = 30,
            image = null,
            role = Role.READER
        )

        // Mocking the response from authService
        Mockito.`when`(authService.register(registerRequest))
            .thenReturn(Pair(userResponse, "User registered successfully"))

        // Performing the mock MVC request
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User registered successfully"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.user.name").value("John Doe"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.user.email").value("john.doe@example.com"))
    }

    // Test case for user login
    @Test
    fun `should login user successfully`() {
        val loginRequest = LoginRequest(
            email = "john.doe@example.com",
            password = "password123"
        )

        val userResponse = UserResponse(
            id = 1L,
            name = "John Doe",
            email = "john.doe@example.com",
            age = 30,
            image = null,
            role = Role.READER
        )

        // Fake the JWT token as response
        val token = "fake-jwt-token"
        Mockito.`when`(authService.login(loginRequest)).thenReturn(Pair(userResponse, token))

        // Performing the mock MVC request
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(token))
            .andExpect(MockMvcResultMatchers.jsonPath("$.user.email").value("john.doe@example.com"))
    }

    // Test case for invalid email during registration
    @Test
    fun `should return bad request when invalid email is provided`() {
        val registerRequest = RegisterRequest(
            name = "John",
            email = "invalid-email",  // Invalid email for testing
            password = "password123",
            role = Role.READER,
            age = 30,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Validation failed"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("email: Invalid email format"))
    }


    @Test
    fun `should return bad request when required fields are missing`() {
        val registerRequest = RegisterRequest(
            name = "", // Missing name
            email = "john.doe@example.com",
            password = "password123",
            role = Role.READER,
            age = 30,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Validation failed"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("name: Name is required"))

    }



    @Test
    fun `should return bad request when password length is less then 6 `() {
        val registerRequest = RegisterRequest(
            name = "Sahil Khan",
            email = "john.doe@example.com",
            password = "pass",
            role = Role.READER,
            age = 30,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Validation failed"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("password: Password should be at least 6 characters long"))

    }

    @Test
    fun `should return bad request when password is missing`() {
        val registerRequest = RegisterRequest(
            name = "Sahil Khan",
            email = "john.doe@example.com",
            password = "",  // Missing password for testing
            role = Role.READER,
            age = 30,
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Validation failed"))
            // Ensure the message contains both error messages for missing password and length check
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(org.hamcrest.Matchers.containsString("password: Password is required")))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(org.hamcrest.Matchers.containsString("password: Password should be at least 6 characters long")))
    }


}
