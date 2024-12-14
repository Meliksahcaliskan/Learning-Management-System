package com.lsm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsm.exception.*;
import com.lsm.mapper.UserMapper;
import com.lsm.model.DTOs.auth.*;
import com.lsm.model.DTOs.TokenRefreshResult;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import com.lsm.security.RateLimiter;
import com.lsm.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    @Mock
    private RateLimiter rateLimiter;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthController authController;

    private LoginRequestDTO loginRequest;
    private RegisterRequestDTO registerRequest;
    private AppUser testUser;
    private LoginResponseDTO loginResponse;
    private RegisterResponseDTO registerResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .build();
        objectMapper = new ObjectMapper();

        // Setup test user
        testUser = AppUser.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role(Role.ROLE_STUDENT)
                .build();

        // Setup login request
        loginRequest = LoginRequestDTO.builder()
                .username("testuser")
                .password("password123")
                .build();

        // Setup register request
        registerRequest = RegisterRequestDTO.builder()
                .username("newuser")
                .password("Password123!")
                .email("new@example.com")
                .role(Role.ROLE_STUDENT)
                .firstName("New")
                .lastName("User")
                .build();

        // Setup login response
        loginResponse = LoginResponseDTO.builder()
                .id(1L)
                .username("testuser")
                .accessToken("test-access-token")
                .refreshToken("test-refresh-token")
                .build();

        // Setup register response
        registerResponse = RegisterResponseDTO.builder()
                .userId(1L)
                .username("newuser")
                .build();
    }

    @Test
    void login_WithValidCredentials_ShouldReturnTokens() throws Exception {
        // Arrange
        AuthenticationResult authResult = new AuthenticationResult(
                "access-token",
                "refresh-token",
                testUser,
                3600000L
        );

        when(authService.authenticate(any(LoginRequestDTO.class), any(String.class)))
                .thenReturn(authResult);
        when(userMapper.toLoginResponse(eq(testUser), any(), any(), any()))
                .thenReturn(loginResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("test-access-token"));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        when(authService.authenticate(any(LoginRequestDTO.class), any(String.class)))
                .thenThrow(new AuthenticationException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void login_WhenRateLimitExceeded_ShouldReturnTooManyRequests() throws Exception {
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
    void register_WithValidData_ShouldReturnSuccess() throws Exception {
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
                .andExpect(jsonPath("$.data.username").value("newuser"));
    }

    @Test
    void register_WithDuplicateUsername_ShouldReturnConflict() throws Exception {
        // Arrange
        when(authService.registerUser(any(RegisterRequestDTO.class)))
                .thenThrow(new DuplicateResourceException("Username already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void refreshToken_WithValidToken_ShouldReturnNewAccessToken() throws Exception {
        // Arrange
        TokenRefreshResult refreshResult = new TokenRefreshResult("new-access-token", "refresh-token");
        when(authService.refreshToken(anyString())).thenReturn(refreshResult);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .header("Refresh-Token", "valid-refresh-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"));
    }

    @Test
    void refreshToken_WithInvalidToken_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        when(authService.refreshToken(anyString()))
                .thenThrow(new InvalidTokenException("Invalid refresh token"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .header("Refresh-Token", "invalid-refresh-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void logout_WithValidToken_ShouldReturnSuccess() throws Exception {
        // Arrange
        doNothing().when(authService).logout(anyString(), anyString());

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer valid-token")
                        .header("Refresh-Token", "valid-refresh-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void logout_WithError_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        doThrow(new LogoutException("Error during logout"))
                .when(authService).logout(anyString(), anyString());

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer valid-token")
                        .header("Refresh-Token", "valid-refresh-token"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }
}