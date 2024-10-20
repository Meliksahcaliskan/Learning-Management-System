package com.lsm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lsm.model.DTOs.LoginRequestDTO;
import com.lsm.model.DTOs.RegisterRequestDTO;
import com.lsm.model.entity.base.AppUser;
import com.lsm.service.AuthService;
import com.lsm.service.JwtTokenProvider;

@WebMvcTest(AuthController.class)
// @ContextConfiguration(classes = {AuthControllerTest.TestConfig.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin_Success() throws Exception {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");

        // Mock the behavior of authService and jwtTokenProvider
        AppUser mockUser = new AppUser(); // Assuming AppUser has a default constructor
        mockUser.setUsername("user");
        when(authService.authenticate(any(LoginRequestDTO.class))).thenReturn(mockUser);
        when(jwtTokenProvider.generateToken(mockUser)).thenReturn("mock-jwt-token");

        // Perform the request and validate response
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"user\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    public void testLogin_BadCredentials() throws Exception {
        // Simulate authentication failure
        when(authService.authenticate(any(LoginRequestDTO.class))).thenThrow(new IllegalArgumentException("Bad credentials"));

        // Perform the request and validate the response
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"wrongUser\",\"password\":\"wrongPassword\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testRegister_Success() throws Exception {
        // RegisterRequestDTO registerRequest = new RegisterRequestDTO("newuser", "password", "email@example.com", Role.STUDENT);

        // Mock the behavior of authService for a successful registration
        AppUser mockUser = new AppUser();
        mockUser.setId(1L); // Assuming AppUser has an ID field
        when(authService.registerUser(any(RegisterRequestDTO.class))).thenReturn(mockUser);

        // Perform the request and validate response
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"newuser\",\"password\":\"password\",\"email\":\"email@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registered successfully"));
    }

    @Test
    public void testRegister_UsernameAlreadyExists() throws Exception {
        // RegisterRequestDTO registerRequest = new RegisterRequestDTO("existinguser", "password", "email@example.com", Role.STUDENT);

        // Simulate username already exists
        when(authService.registerUser(any(RegisterRequestDTO.class))).thenThrow(new IllegalArgumentException("Username already exists"));

        // Perform the request and validate response
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"existinguser\",\"password\":\"password\",\"email\":\"email@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    /*
    @Configuration
    static class TestConfig {
        @Bean
        public AuthenticationManager authenticationManager() {
            return new TestingAuthenticationManager();
        }
    }
    */
}
