package com.lsm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ContentController.class)
public class ContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private ContentController contentController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginPage() throws Exception {
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void testSignupPage() throws Exception {
        mockMvc.perform(get("/api/auth/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"));
    }

    @Test
    public void testHomePage() throws Exception {
        mockMvc.perform(get("/api/auth/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}
