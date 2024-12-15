package com.lsm.security;

import com.lsm.model.DTOs.AssignmentRequestDTO;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsm.config.TestRedisConfig;
import com.lsm.model.DTOs.auth.LoginRequestDTO;
import com.lsm.service.*;
import com.lsm.events.EventPublisher;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class SQLInjectionSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private ClassEntityService classEntityService;

    @MockBean
    private AssignmentService assignmentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AssignmentDocumentService assignmentDocumentService;

    @MockBean
    private EventPublisher eventPublisher;

    @MockBean
    private LoginAttemptService loginAttemptService;

    @MockBean
    private RateLimiter rateLimiter;
    @Autowired
    private AppUserService appUserService;

    @BeforeEach
    public void setup() {
        when(loginAttemptService.isBlocked(any())).thenReturn(false);
        doNothing().when(rateLimiter).checkRateLimit(any());
    }

    @Test
    public void testAuthenticationEndpointSQLInjection() throws Exception {
        String[] maliciousInputs = {
                "' OR '1'='1",
                "admin' --",
                "' UNION SELECT * FROM app_user--",
                "'; DROP TABLE app_user--"
        };

        for (String maliciousInput : maliciousInputs) {
            LoginRequestDTO loginRequest = new LoginRequestDTO();
            loginRequest.setUsername(maliciousInput);
            loginRequest.setPassword(maliciousInput);

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/v1/auth/login")
                            .content(objectMapper.writeValueAsString(loginRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest()); // Changed to expect 400
        }
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testAssignmentEndpointSQLInjection() throws Exception {
        // Create mock AppUser
        AppUser mockUser = AppUser.builder()
                .id(1L)
                .username("admin")
                .role(Role.ROLE_ADMIN)
                .build();

        // Mock the authenticationService response
        when(appUserService.getCurrentUserWithDetails(any()))
                .thenReturn(mockUser);
        when(assignmentService.getAllAssignments(any()))
                .thenReturn(new ArrayList<>());

        String[] maliciousInputs = {
                "'; DROP TABLE assignment--",
                "' UNION SELECT * FROM assignment--",
                "' OR '1'='1"
        };

        for (String maliciousInput : maliciousInputs) {
            // Mock Authentication object
            Authentication authentication = new TestingAuthenticationToken(mockUser, null, "ROLE_ADMIN");
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Test GET assignments endpoint
            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/v1/assignments")
                            .principal(authentication)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string(not(containsString("SQLException"))))
                    .andExpect(content().string(not(containsString("org.hibernate"))));

            // Test teacher assignments endpoint
            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/v1/assignments/teacher/{teacherId}", maliciousInput)
                            .principal(authentication)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(not(containsString("SQLException"))))
                    .andExpect(content().string(not(containsString("org.hibernate"))));

            // Test create assignment
            AssignmentRequestDTO requestDTO = AssignmentRequestDTO.builder()
                    .title(maliciousInput)
                    .description("Test Description")
                    .classId(1L)
                    .courseId(1L)
                    .dueDate(LocalDate.now().plusDays(7))
                    .build();

            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/v1/assignments/createAssignment")
                            .principal(authentication)
                            .content(objectMapper.writeValueAsString(requestDTO))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(not(containsString("SQLException"))))
                    .andExpect(content().string(not(containsString("org.hibernate"))));
        }
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    public void testClassEntityEndpointSQLInjection() throws Exception {
        String[] maliciousInputs = {
                "'; DROP TABLE class_entity--",
                "' UNION SELECT * FROM class_entity--",
                "' OR '1'='1"
        };

        for (String maliciousInput : maliciousInputs) {
            mockMvc.perform(MockMvcRequestBuilders
                            .get("/api/v1/classes/{id}", maliciousInput)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(not(containsString("SQLException"))))
                    .andExpect(content().string(not(containsString("org.hibernate"))));
        }
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    public void testErrorResponseSecurity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/classes/{id}", "' OR '1'='1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(not(containsString("SQLException"))))
                .andExpect(content().string(not(containsString("org.hibernate"))))
                .andExpect(content().string(not(containsString("jdbc"))));
    }
}