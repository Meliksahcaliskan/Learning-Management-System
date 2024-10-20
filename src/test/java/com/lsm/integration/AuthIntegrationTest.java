package com.lsm.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsm.model.DTOs.LoginRequestDTO;
import com.lsm.model.DTOs.RegisterRequestDTO;
import com.lsm.model.entity.base.AppUser;
import com.lsm.repository.AppUserRepository;

@SpringBootTest
public class AuthIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AppUserRepository appUserRepository;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        appUserRepository.deleteAll(); // Clean up before each test
    }

    @Test
    public void testRegisterUser_Success() throws Exception {
        RegisterRequestDTO registerRequest = new RegisterRequestDTO();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password");
        registerRequest.setEmail("newuser@example.com");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Registered successfully"))
                .andExpect(jsonPath("$.userId").isNotEmpty());
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        // First, register a user
        AppUser appUser = new AppUser();
        appUser.setUsername("testuser");
        appUser.setPassword("password");  // Assuming password will be encrypted
        appUser.setEmail("testuser@example.com");
        appUserRepository.save(appUser);

        // Now try to login with this user
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    public void testLoginUser_BadCredentials() throws Exception {
        // Try to login with wrong credentials
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("wronguser");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
