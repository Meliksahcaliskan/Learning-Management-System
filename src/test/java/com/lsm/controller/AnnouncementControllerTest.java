package com.lsm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsm.model.DTOs.AnnouncementDTO;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import com.lsm.service.AnnouncementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnnouncementController.class)
class AnnouncementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AnnouncementService announcementService;

    private AnnouncementDTO announcementDTO;
    private AppUser teacherUser;
    private AppUser studentUser;
    private AppUser adminUser;

    @BeforeEach
    void setUp() {
        // Setup announcement DTO
        announcementDTO = new AnnouncementDTO();
        announcementDTO.setId(1L);
        announcementDTO.setTitle("Test Announcement");
        announcementDTO.setContent("Test Content");
        announcementDTO.setClassId(1L);
        announcementDTO.setCreatedAt(LocalDate.now());

        // Setup users
        teacherUser = AppUser.builder()
                .id(1L)
                .username("teacher")
                .role(Role.ROLE_TEACHER)
                .build();

        studentUser = AppUser.builder()
                .id(2L)
                .username("student")
                .role(Role.ROLE_STUDENT)
                .build();

        adminUser = AppUser.builder()
                .id(3L)
                .username("admin")
                .role(Role.ROLE_ADMIN)
                .build();
    }

    @Nested
    @DisplayName("Create Announcement Tests")
    class CreateAnnouncementTests {

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should create announcement successfully")
        void shouldCreateAnnouncement() throws Exception {
            when(announcementService.createAnnouncement(any(), any()))
                    .thenReturn(announcementDTO);

            mockMvc.perform(post("/api/v1/announcements")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(announcementDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.title").value(announcementDTO.getTitle()));
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should not allow student to create announcement")
        void shouldNotAllowStudentToCreateAnnouncement() throws Exception {
            mockMvc.perform(post("/api/v1/announcements")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(announcementDTO)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should handle access denied when creating announcement")
        void shouldHandleAccessDeniedWhenCreating() throws Exception {
            when(announcementService.createAnnouncement(any(), any()))
                    .thenThrow(new AccessDeniedException("Access denied"));

            mockMvc.perform(post("/api/v1/announcements")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(announcementDTO)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("Get Announcements Tests")
    class GetAnnouncementsTests {

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should get announcements by class")
        void shouldGetAnnouncementsByClass() throws Exception {
            List<AnnouncementDTO> announcements = Arrays.asList(announcementDTO);
            when(announcementService.getAnnouncementsByClassId(any(), eq(1L)))
                    .thenReturn(announcements);

            mockMvc.perform(get("/api/v1/announcements/class/1")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data[0].title").value(announcementDTO.getTitle()));
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should handle access denied when getting announcements")
        void shouldHandleAccessDeniedWhenGetting() throws Exception {
            when(announcementService.getAnnouncementsByClassId(any(), eq(1L)))
                    .thenThrow(new AccessDeniedException("Access denied"));

            mockMvc.perform(get("/api/v1/announcements/class/1")
                            .with(csrf()))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("Update Announcement Tests")
    class UpdateAnnouncementTests {

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should update announcement successfully")
        void shouldUpdateAnnouncement() throws Exception {
            when(announcementService.updateAnnouncement(any(), eq(1L), any()))
                    .thenReturn(announcementDTO);

            mockMvc.perform(put("/api/v1/announcements/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(announcementDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.title").value(announcementDTO.getTitle()));
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should not allow student to update announcement")
        void shouldNotAllowStudentToUpdateAnnouncement() throws Exception {
            mockMvc.perform(put("/api/v1/announcements/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(announcementDTO)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should handle access denied when updating announcement")
        void shouldHandleAccessDeniedWhenUpdating() throws Exception {
            when(announcementService.updateAnnouncement(any(), eq(1L), any()))
                    .thenThrow(new AccessDeniedException("Access denied"));

            mockMvc.perform(put("/api/v1/announcements/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(announcementDTO)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("Delete Announcement Tests")
    class DeleteAnnouncementTests {

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should delete announcement successfully")
        void shouldDeleteAnnouncement() throws Exception {
            doNothing().when(announcementService).deleteAnnouncement(any(), eq(1L));

            mockMvc.perform(delete("/api/v1/announcements/1")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should not allow student to delete announcement")
        void shouldNotAllowStudentToDeleteAnnouncement() throws Exception {
            mockMvc.perform(delete("/api/v1/announcements/1")
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should handle access denied when deleting announcement")
        void shouldHandleAccessDeniedWhenDeleting() throws Exception {
            doThrow(new AccessDeniedException("Access denied"))
                    .when(announcementService).deleteAnnouncement(any(), eq(1L));

            mockMvc.perform(delete("/api/v1/announcements/1")
                            .with(csrf()))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }
}