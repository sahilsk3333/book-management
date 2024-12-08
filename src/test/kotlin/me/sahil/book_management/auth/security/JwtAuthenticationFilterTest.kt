package me.sahil.book_management.auth.security

import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthenticationFilterTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var jwtTokenProvider: JwtTokenProvider // Mock JwtTokenProvider

    @Test
    fun `should return unauthorized if token is missing`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/some-secure-endpoint"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Login is required"))
    }

    @Test
    fun `should return unauthorized if token is invalid`() {
        // Mock the token validation to return false
        `when`(jwtTokenProvider.validateToken("invalid_token")).thenReturn(false)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/some-secure-endpoint")
            .header("Authorization", "Bearer invalid_token"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Unauthorized"))
    }

}
