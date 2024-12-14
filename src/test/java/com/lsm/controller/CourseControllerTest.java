package com.lsm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsm.model.DTOs.CourseDTO;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import com.lsm.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CourseService courseService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CourseController courseController;

    private CourseDTO testCourse;
    private AppUser adminUser;
    private AppUser teacherUser;
    private AppUser studentUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(courseController)
                .build();
        objectMapper = new ObjectMapper();

        // Setup test course
        testCourse = CourseDTO.builder()
                .id(1L)
                .name("Test Course")
                .code("TC101")
                .description("Test Description")
                .credits(3)
                .classEntityIds(Arrays.asList(1L, 2L))
                .build();

        // Setup users with different roles
        adminUser = AppUser.builder()
                .id(1L)
                .role(Role.ROLE_ADMIN)
                .build();

        teacherUser = AppUser.builder()
                .id(2L)
                .role(Role.ROLE_TEACHER)
                .build();

        studentUser = AppUser.builder()
                .id(3L)
                .role(Role.ROLE_STUDENT)
                .build();
    }

    @Test
    void getCourseById_WithValidId_ShouldReturnCourse() throws Exception {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(courseService.getCourseById(eq(adminUser), eq(1L))).thenReturn(testCourse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/courses/1")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value(testCourse.getName()));
    }

    @Test
    void getCourseById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(adminUser);
        when(courseService.getCourseById(eq(adminUser), eq(999L)))
                .thenThrow(new EntityNotFoundException("Course not found"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/courses/999")
                        .principal(authentication))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getAllCourses_AsAdmin_ShouldReturnAllCourses() throws Exception {
        // Arrange
        List<CourseDTO> courses = Arrays.asList(testCourse);
        when(courseService.getAllCourses()).thenReturn(courses);

        // Act & Assert
        mockMvc.perform(get("/api/v1/courses")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value(testCourse.getName()));
    }

    @Test
    void createCourse_WithValidData_ShouldCreateCourse() throws Exception {
        // Arrange
        when(courseService.createCourse(any(CourseDTO.class))).thenReturn(testCourse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCourse))
                        .principal(authentication))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value(testCourse.getName()));
    }

    @Test
    void updateCourse_WithValidData_ShouldUpdateCourse() throws Exception {
        // Arrange
        when(courseService.updateCourse(eq(1L), any(CourseDTO.class))).thenReturn(testCourse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCourse))
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value(testCourse.getName()));
    }

    @Test
    void deleteCourse_WithValidId_ShouldDeleteCourse() throws Exception {
        // Arrange
        doNothing().when(courseService).deleteCourse(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/courses/1")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getCoursesByClassId_WithValidId_ShouldReturnCourses() throws Exception {
        // Arrange
        List<CourseDTO> courses = Collections.singletonList(testCourse);
        when(courseService.getCoursesByClassId(1L)).thenReturn(courses);

        // Act & Assert
        mockMvc.perform(get("/api/v1/courses/class/1")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value(testCourse.getName()));
    }

    @Test
    void getCoursesByStudent_AsValidStudent_ShouldReturnCourses() throws Exception {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(studentUser);
        List<CourseDTO> courses = Collections.singletonList(testCourse);
        when(courseService.getCoursesByStudent(3L)).thenReturn(courses);

        // Act & Assert
        mockMvc.perform(get("/api/v1/courses/student/3")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getCoursesByStudent_AsWrongStudent_ShouldReturnForbidden() throws Exception {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(studentUser);

        // Act & Assert
        mockMvc.perform(get("/api/v1/courses/student/4")
                        .principal(authentication))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCoursesByTeacher_AsValidTeacher_ShouldReturnCourses() throws Exception {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(teacherUser);
        List<CourseDTO> courses = Collections.singletonList(testCourse);
        when(courseService.getCoursesByTeacher(2L)).thenReturn(courses);

        // Act & Assert
        mockMvc.perform(get("/api/v1/courses/teacher/2")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void searchCourses_WithValidParameters_ShouldReturnResults() throws Exception {
        // Arrange
        List<CourseDTO> courses = Collections.singletonList(testCourse);
        when(courseService.searchCourses("Test", "Spring", 2024)).thenReturn(courses);

        // Act & Assert
        mockMvc.perform(get("/api/v1/courses/search")
                        .param("query", "Test")
                        .param("semester", "Spring")
                        .param("year", "2024")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value(testCourse.getName()));
    }
}