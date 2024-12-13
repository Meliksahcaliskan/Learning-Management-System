package com.lsm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsm.model.DTOs.CourseDTO;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import com.lsm.service.CourseService;
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

import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    private CourseDTO courseDTO;
    private AppUser teacherUser;
    private AppUser studentUser;
    private AppUser adminUser;

    @BeforeEach
    void setUp() {
        // Setup course DTO
        courseDTO = CourseDTO.builder()
                .id(1L)
                .name("Test Course")
                .code("TEST101")
                .credits(3)
                .description("Test Description")
                .classEntityIds(Collections.singletonList(1L))
                .build();

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
    @DisplayName("Get Course Tests")
    class GetCourseTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should get course by ID")
        void shouldGetCourseById() throws Exception {
            when(courseService.getCourseById(any(), eq(1L))).thenReturn(courseDTO);

            mockMvc.perform(get("/api/v1/courses/1")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value(courseDTO.getName()));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should handle course not found")
        void shouldHandleCourseNotFound() throws Exception {
            when(courseService.getCourseById(any(), eq(1L)))
                    .thenThrow(new EntityNotFoundException("Course not found"));

            mockMvc.perform(get("/api/v1/courses/1")
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should get all courses")
        void shouldGetAllCourses() throws Exception {
            List<CourseDTO> courses = Arrays.asList(courseDTO);
            when(courseService.getAllCourses()).thenReturn(courses);

            mockMvc.perform(get("/api/v1/courses")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].name").value(courseDTO.getName()));
        }
    }

    @Nested
    @DisplayName("Create Course Tests")
    class CreateCourseTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should create course successfully")
        void shouldCreateCourse() throws Exception {
            when(courseService.createCourse(any(CourseDTO.class))).thenReturn(courseDTO);

            mockMvc.perform(post("/api/v1/courses")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(courseDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value(courseDTO.getName()));
        }

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should not allow teacher to create course")
        void shouldNotAllowTeacherToCreateCourse() throws Exception {
            mockMvc.perform(post("/api/v1/courses")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(courseDTO)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Update Course Tests")
    class UpdateCourseTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should update course successfully")
        void shouldUpdateCourse() throws Exception {
            when(courseService.updateCourse(eq(1L), any(CourseDTO.class))).thenReturn(courseDTO);

            mockMvc.perform(put("/api/v1/courses/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(courseDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value(courseDTO.getName()));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should handle course not found during update")
        void shouldHandleCourseNotFoundDuringUpdate() throws Exception {
            when(courseService.updateCourse(eq(1L), any(CourseDTO.class)))
                    .thenThrow(new EntityNotFoundException("Course not found"));

            mockMvc.perform(put("/api/v1/courses/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(courseDTO)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("Delete Course Tests")
    class DeleteCourseTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should delete course successfully")
        void shouldDeleteCourse() throws Exception {
            doNothing().when(courseService).deleteCourse(1L);

            mockMvc.perform(delete("/api/v1/courses/1")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("Course Search Tests")
    class CourseSearchTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should search courses successfully")
        void shouldSearchCourses() throws Exception {
            List<CourseDTO> searchResults = Arrays.asList(courseDTO);
            when(courseService.searchCourses(any(), any(), any())).thenReturn(searchResults);

            mockMvc.perform(get("/api/v1/courses/search")
                            .param("query", "test")
                            .param("semester", "FALL")
                            .param("year", "2024")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("Course By Teacher Tests")
    class CourseByTeacherTests {

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should get teacher courses")
        void shouldGetTeacherCourses() throws Exception {
            List<CourseDTO> teacherCourses = Arrays.asList(courseDTO);
            when(courseService.getCoursesByTeacher(1L)).thenReturn(teacherCourses);

            mockMvc.perform(get("/api/v1/courses/teacher/1")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should not allow teacher to view other teacher's courses")
        void shouldNotAllowTeacherToViewOtherTeachersCourses() throws Exception {
            mockMvc.perform(get("/api/v1/courses/teacher/2")
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Course By Student Tests")
    class CourseByStudentTests {

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should get student courses")
        void shouldGetStudentCourses() throws Exception {
            List<CourseDTO> studentCourses = Arrays.asList(courseDTO);
            when(courseService.getCoursesByStudent(1L)).thenReturn(studentCourses);

            mockMvc.perform(get("/api/v1/courses/student/1")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should not allow student to view other student's courses")
        void shouldNotAllowStudentToViewOtherStudentsCourses() throws Exception {
            mockMvc.perform(get("/api/v1/courses/student/2")
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }
}