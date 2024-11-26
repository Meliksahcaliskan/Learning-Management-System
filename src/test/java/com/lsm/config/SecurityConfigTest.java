package com.lsm.config;

import com.lsm.service.AppUserService;
import com.lsm.service.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SecurityConfig.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppUserService appUserService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private HandlerExceptionResolver handlerExceptionResolver;

    private UserDetails testTeacher;
    private UserDetails testStudent;
    private String validToken;

    @BeforeEach
    void setUp() {
        // Setup test users
        testTeacher = new User("teacher@test.com", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER")));
        testStudent = new User("student@test.com", "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT")));

        validToken = "valid.jwt.token";

        // Configure JWT token provider mock
        when(jwtTokenProvider.validateToken(validToken)).thenReturn(true);
        when(jwtTokenProvider.getAuthentication(validToken))
                .thenReturn(new UsernamePasswordAuthenticationToken(testTeacher, null, testTeacher.getAuthorities()));
    }

    @Test
    void whenAccessingPublicEndpoints_thenSuccess() throws Exception {
        List<String> publicEndpoints = List.of(
                "/api/v1/auth/register",
                "/api/v1/auth/login",
                "/api/v1/auth/refresh",
                "/health",
                "/swagger-ui.html",
                "/v3/api-docs"
        );

        for (String endpoint : publicEndpoints) {
            mockMvc.perform(get(endpoint))
                    .andExpect(status().isNotFound()); // Expect 404 since endpoints don't exist in test context
        }
    }

    @Test
    void whenAccessingProtectedEndpointWithoutAuth_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/assignments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void whenTeacherAccessesAssignmentEndpoints_thenSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/assignments/create"))
                .andExpect(status().isNotFound()); // 404 since endpoint doesn't exist in test context

        mockMvc.perform(put("/api/v1/assignments/1"))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/v1/assignments/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void whenStudentAccessesTeacherEndpoints_thenForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/assignments/create"))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/v1/assignments/1"))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/v1/assignments/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void whenStudentAccessesStudentEndpoints_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/assignments/student/1"))
                .andExpect(status().isNotFound()); // 404 since endpoint doesn't exist in test context
    }

    @Test
    void testCorsConfiguration() throws Exception {
        mockMvc.perform(options("/api/v1/assignments")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Methods"))
                .andExpect(header().exists("Access-Control-Allow-Headers"));
    }

    @Test
    void testSecurityHeaders() throws Exception {
        mockMvc.perform(get("/api/v1/auth/login"))
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("X-XSS-Protection", "1; mode=block"))
                .andExpect(header().exists("Content-Security-Policy"));
    }

    @Test
    void whenLogout_thenSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk());
    }
}