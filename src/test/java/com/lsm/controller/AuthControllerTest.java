package com.lsm.controller;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsm.model.DTOs.LoginRequestDTO;
import com.lsm.model.DTOs.RegisterRequestDTO;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import com.lsm.service.AuthService;
import com.lsm.service.JwtTokenProvider;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    @WithMockUser
    public void testRegister_Success() throws Exception {
        // Create request DTO
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("newuser");
        request.setPassword("password");
        request.setEmail("email@example.com");
        request.setRole(Role.STUDENT);

        // Create mock response
        AppUser mockUser = new AppUser();
        // mockUser.setId(1L);
        mockUser.setUsername("newuser");
        mockUser.setEmail("email@example.com");
        mockUser.setRole(Role.STUDENT);

        // Mock service behavior
        when(authService.registerUser(any(RegisterRequestDTO.class))).thenReturn(mockUser);

        // Perform test
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registered successfully"));
    }

    @Test
    public void testRegister_UsernameAlreadyExists() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setUsername("existinguser");
        request.setPassword("password");
        request.setEmail("email@example.com");
        request.setRole(Role.STUDENT);

        when(authService.registerUser(any(RegisterRequestDTO.class)))
            .thenThrow(new IllegalArgumentException("Username already exists"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    @Test
    public void testLogin_Success() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");

        AppUser mockUser = new AppUser();
        mockUser.setUsername("user");
        mockUser.setId(1L);
        
        when(authService.authenticate(any(LoginRequestDTO.class))).thenReturn(mockUser);
        when(jwtTokenProvider.generateToken(any(AppUser.class))).thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    public void testLogin_BadCredentials() throws Exception {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("wrongUser");
        loginRequest.setPassword("wrongPassword");

        // Mock the behavior of authService.authenticate to throw BadCredentialsException
        when(authService.authenticate(any(LoginRequestDTO.class)))
            .thenThrow(new IllegalArgumentException("Bad credentials"));

        // Act and Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

}
