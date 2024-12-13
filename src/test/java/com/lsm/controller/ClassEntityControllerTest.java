package com.lsm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsm.mapper.ClassEntityMapper;
import com.lsm.model.DTOs.ClassEntityRequestDTO;
import com.lsm.model.DTOs.ClassEntityResponseDTO;
import com.lsm.model.entity.ClassEntity;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import com.lsm.service.ClassEntityService;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassEntityController.class)
class ClassEntityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClassEntityService classService;

    @MockBean
    private ClassEntityMapper classMapper;

    private AppUser teacherUser;
    private AppUser studentUser;
    private AppUser adminUser;
    private ClassEntity testClass;
    private ClassEntityRequestDTO requestDTO;
    private ClassEntityResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
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

        // Setup test class
        testClass = ClassEntity.builder()
                .id(1L)
                .name("Test Class")
                .teacher(teacherUser)
                .build();

        // Setup request DTO
        requestDTO = new ClassEntityRequestDTO();
        requestDTO.setName("Test Class");
        requestDTO.setTeacherId(1L);
        requestDTO.setStudentIds(Collections.singletonList(2L));

        // Setup response DTO
        responseDTO = new ClassEntityResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Test Class");
        responseDTO.setTeacherId(1L);
    }

    @Nested
    @DisplayName("Create Class Tests")
    class CreateClassTests {

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should create class successfully")
        void shouldCreateClassSuccessfully() throws Exception {
            // Arrange
            when(classMapper.toEntity(any())).thenReturn(testClass);
            when(classMapper.toDTO(any())).thenReturn(responseDTO);
            when(classService.createClass(any(), any(), any(), any())).thenReturn(testClass);

            // Act & Assert
            mockMvc.perform(post("/api/v1/classes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.name").value("Test Class"));
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should not allow students to create class")
        void shouldNotAllowStudentsToCreateClass() throws Exception {
            mockMvc.perform(post("/api/v1/classes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Get Class Tests")
    class GetClassTests {

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should get class by ID")
        void shouldGetClassById() throws Exception {
            // Arrange
            when(classService.getClassById(any(), eq(1L))).thenReturn(testClass);
            when(classMapper.toDTO(any())).thenReturn(responseDTO);

            // Act & Assert
            mockMvc.perform(get("/api/v1/classes/1")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1L));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should get all classes")
        void shouldGetAllClasses() throws Exception {
            // Arrange
            List<ClassEntity> classes = Arrays.asList(testClass);
            when(classService.getAllClasses(any())).thenReturn(classes);
            when(classMapper.toDTO(any())).thenReturn(responseDTO);

            // Act & Assert
            mockMvc.perform(get("/api/v1/classes")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("Update Class Tests")
    class UpdateClassTests {

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should update class successfully")
        void shouldUpdateClassSuccessfully() throws Exception {
            // Arrange
            when(classMapper.toEntity(any())).thenReturn(testClass);
            when(classMapper.toDTO(any())).thenReturn(responseDTO);
            when(classService.updateClass(any(), any(), any(), any(), any())).thenReturn(testClass);

            // Act & Assert
            mockMvc.perform(put("/api/v1/classes/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should handle access denied during update")
        void shouldHandleAccessDeniedDuringUpdate() throws Exception {
            // Arrange
            when(classMapper.toEntity(any())).thenReturn(testClass);
            when(classService.updateClass(any(), any(), any(), any(), any()))
                    .thenThrow(new AccessDeniedException("Access denied"));

            // Act & Assert
            mockMvc.perform(put("/api/v1/classes/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Student Management Tests")
    class StudentManagementTests {

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should add student successfully")
        void shouldAddStudentSuccessfully() throws Exception {
            // Arrange
            when(classService.addStudent(any(), eq(1L), eq(2L))).thenReturn(testClass);
            when(classMapper.toDTO(any())).thenReturn(responseDTO);

            // Act & Assert
            mockMvc.perform(post("/api/v1/classes/1/students/2")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should add students in bulk successfully")
        void shouldAddStudentsBulkSuccessfully() throws Exception {
            // Arrange
            List<Long> studentIds = Arrays.asList(1L, 2L);
            when(classService.addStudentsBulk(any(), eq(1L), any())).thenReturn(testClass);
            when(classMapper.toDTO(any())).thenReturn(responseDTO);

            // Act & Assert
            mockMvc.perform(post("/api/v1/classes/1/students/bulk")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(studentIds)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should remove student successfully")
        void shouldRemoveStudentSuccessfully() throws Exception {
            // Arrange
            when(classService.removeStudent(any(), eq(1L), eq(2L))).thenReturn(testClass);
            when(classMapper.toDTO(any())).thenReturn(responseDTO);

            // Act & Assert
            mockMvc.perform(delete("/api/v1/classes/1/students/2")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("Teacher and Student Views Tests")
    class TeacherAndStudentViewsTests {

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should get teacher classes")
        void shouldGetTeacherClasses() throws Exception {
            // Arrange
            List<ClassEntity> classes = Arrays.asList(testClass);
            when(classService.getTeacherClasses(any())).thenReturn(classes);
            when(classMapper.toDTO(any())).thenReturn(responseDTO);

            // Act & Assert
            mockMvc.perform(get("/api/v1/classes/teacher")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isArray());
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should get student classes")
        void shouldGetStudentClasses() throws Exception {
            // Arrange
            when(classService.getStudentClasses(any())).thenReturn(testClass);
            when(classMapper.toDTO(any())).thenReturn(responseDTO);

            // Act & Assert
            mockMvc.perform(get("/api/v1/classes/student")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }
}