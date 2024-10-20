package com.lsm.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class ContentIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testLoginPage_ReturnsLoginView() throws Exception {
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(status().isOk())               // Expect HTTP 200 OK
                .andExpect(view().name("login"));         // Expect the view name to be "login"
    }

    @Test
    public void testSignupPage_ReturnsSignupView() throws Exception {
        mockMvc.perform(get("/api/auth/signup"))
                .andExpect(status().isOk())               // Expect HTTP 200 OK
                .andExpect(view().name("signup"));        // Expect the view name to be "signup"
    }

    @Test
    public void testHomePage_ReturnsIndexView() throws Exception {
        mockMvc.perform(get("/api/auth/index"))
                .andExpect(status().isOk())               // Expect HTTP 200 OK
                .andExpect(view().name("index"));         // Expect the view name to be "index"
    }
}
