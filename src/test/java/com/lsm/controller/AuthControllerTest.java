package com.lsm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsm.exception.*;
import com.lsm.mapper.UserMapper;
import com.lsm.model.DTOs.TokenRefreshResult;
import com.lsm.model.DTOs.auth.*;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import com.lsm.security.RateLimiter;
import com.lsm.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private RateLimiter rateLimiter;

    @MockBean
    private UserMapper userMapper;

    private LoginRequestDTO loginRequest;
    private RegisterRequestDTO registerRequest;
    private AppUser testUser;
    private LoginResponseDTO loginResponse;
    private RegisterResponseDTO registerResponse;

    @BeforeEach
    void setUp() {
        // Setup login request
        loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // Setup register request
        registerRequest = new RegisterRequestDTO();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@test.com");
        registerRequest.setPassword("password123");

        // Setup test user
        testUser = AppUser.builder()
                .id(1L)
                .username("testuser")
                .email("test@test.com")
                .role(Role.ROLE_STUDENT)
                .build();

        // Setup login response
        loginResponse = LoginResponseDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@test.com")
                .role(Role.ROLE_STUDENT)
                .accessToken("test-access-token")
                .refreshToken("test-refresh-token")
                .build();

        // Setup register response
        registerResponse = RegisterResponseDTO.builder()
                .userId(1L)
                .username("newuser")
                .email("newuser@test.com")
                .role(Role.ROLE_STUDENT)
                .build();
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully")
        void shouldLoginSuccessfully() throws Exception {
            // Arrange
            AuthenticationResult authResult = new AuthenticationResult(
                    "test-access-token", "test-refresh-token", testUser, 3600L);

            when(authService.authenticate(any(LoginRequestDTO.class), any(String.class)))
                    .thenReturn(authResult);
            when(userMapper.toLoginResponse(eq(testUser), any(), any(), any()))
                    .thenReturn(loginResponse);
            doNothing().when(rateLimiter).checkRateLimit(any());

            // Act & Assert
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.username").value(loginResponse.getUsername()));
        }

        @Test
        @DisplayName("Should handle rate limit exceeded")
        void shouldHandleRateLimitExceeded() throws Exception {
            // Arrange
            doThrow(new RateLimitExceededException("Too many attempts"))
                    .when(rateLimiter).checkRateLimit(any());

            // Act & Assert
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isTooManyRequests())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @DisplayName("Should handle invalid credentials")
        void shouldHandleInvalidCredentials() throws Exception {
            // Arrange
            doNothing().when(rateLimiter).checkRateLimit(any());
            when(authService.authenticate(any(), any()))
                    .thenThrow(new AuthenticationException("Invalid credentials"));

            // Act & Assert
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Should register successfully")
        void shouldRegisterSuccessfully() throws Exception {
            // Arrange
            when(authService.registerUser(any(RegisterRequestDTO.class)))
                    .thenReturn(testUser);
            when(userMapper.toRegisterResponse(testUser))
                    .thenReturn(registerResponse);

            // Act & Assert
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.username").value(registerResponse.getUsername()));
        }

        @Test
        @DisplayName("Should handle duplicate username")
        void shouldHandleDuplicateUsername() throws Exception {
            // Arrange
            when(authService.registerUser(any()))
                    .thenThrow(new DuplicateResourceException("Username already exists"));

            // Act & Assert
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("Token Tests")
    class TokenTests {

        @Test
        @DisplayName("Should refresh token successfully")
        void shouldRefreshTokenSuccessfully() throws Exception {
            // Arrange
            TokenRefreshResult refreshResult = new TokenRefreshResult(
                    "new-access-token", "new-refresh-token");
            when(authService.refreshToken(any()))
                    .thenReturn(refreshResult);

            // Act & Assert
            mockMvc.perform(post("/api/v1/auth/refresh")
                            .header("Refresh-Token", "old-refresh-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.accessToken").value("new-access-token"));
        }

        @Test
        @DisplayName("Should handle invalid refresh token")
        void shouldHandleInvalidRefreshToken() throws Exception {
            // Arrange
            when(authService.refreshToken(any()))
                    .thenThrow(new InvalidTokenException("Invalid refresh token"));

            // Act & Assert
            mockMvc.perform(post("/api/v1/auth/refresh")
                            .header("Refresh-Token", "invalid-token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("Logout Tests")
    class LogoutTests {

        @Test
        @DisplayName("Should logout successfully")
        void shouldLogoutSuccessfully() throws Exception {
            // Arrange
            doNothing().when(authService).logout(any(), any());

            // Act & Assert
            mockMvc.perform(post("/api/v1/auth/logout")
                            .header("Authorization", "Bearer test-token")
                            .header("Refresh-Token", "test-refresh-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @DisplayName("Should handle logout failure")
        void shouldHandleLogoutFailure() throws Exception {
            // Arrange
            doThrow(new LogoutException("Logout failed"))
                    .when(authService).logout(any(), any());

            // Act & Assert
            mockMvc.perform(post("/api/v1/auth/logout")
                            .header("Authorization", "Bearer test-token"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }
}