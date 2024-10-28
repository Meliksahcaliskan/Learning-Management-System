package com.lsm.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.lsm.service.AppUserService;

//@WebMvcTest(ContentController.class)
//@AutoConfigureMockMvc(addFilters = false)
public class ContentControllerTest {
/*
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppUserService appUserService; 

    @InjectMocks
    private ContentController contentController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser
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
*/
}
