package com.lsm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsm.model.DTOs.*;
import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.AssignmentDocument;
import com.lsm.model.entity.StudentSubmission;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.AssignmentStatus;
import com.lsm.model.entity.enums.Role;
import com.lsm.service.AssignmentDocumentService;
import com.lsm.service.AssignmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssignmentController.class)
class AssignmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AssignmentService assignmentService;

    @MockBean
    private AssignmentDocumentService documentService;

    private AppUser teacherUser;
    private AppUser studentUser;
    private Assignment testAssignment;
    private AssignmentRequestDTO testAssignmentRequest;
    private AssignmentDocument testDocument;

    @BeforeEach
    void setUp() {
        // Setup teacher user
        teacherUser = AppUser.builder()
                .id(1L)
                .username("teacher")
                .role(Role.ROLE_TEACHER)
                .build();

        // Setup student user
        studentUser = AppUser.builder()
                .id(2L)
                .username("student")
                .role(Role.ROLE_STUDENT)
                .build();

        // Setup test assignment
        testAssignment = Assignment.builder()
                .id(1L)
                .title("Test Assignment")
                .description("Test Description")
                .dueDate(LocalDate.now().plusDays(7))
                .assignedBy(teacherUser)
                .build();

        // Setup test assignment request
        testAssignmentRequest = new AssignmentRequestDTO();
        testAssignmentRequest.setTitle("Test Assignment");
        testAssignmentRequest.setDescription("Test Description");
        testAssignmentRequest.setDueDate(LocalDate.now().plusDays(7));
        testAssignmentRequest.setClassId(1L);
        testAssignmentRequest.setCourseId(1L);

        // Setup test document
        testDocument = AssignmentDocument.builder()
                .id(1L)
                .fileName("test.pdf")
                .fileType("application/pdf")
                .uploadTime(LocalDateTime.now())
                .uploadedBy(teacherUser)
                .assignment(testAssignment)
                .build();
    }

    @Nested
    @DisplayName("Create Assignment Tests")
    class CreateAssignmentTests {

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should create assignment successfully")
        void shouldCreateAssignment() throws Exception {
            when(assignmentService.createAssignment(any(), any()))
                    .thenReturn(testAssignment);

            mockMvc.perform(post("/api/v1/assignments/createAssignment")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testAssignmentRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true));
        }

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should not allow students to create assignment")
        void shouldNotAllowStudentToCreateAssignment() throws Exception {
            mockMvc.perform(post("/api/v1/assignments/createAssignment")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(testAssignmentRequest)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Upload Document Tests")
    class UploadDocumentTests {

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should upload document successfully")
        void shouldUploadDocument() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test.pdf",
                    "application/pdf",
                    "test content".getBytes()
            );

            when(documentService.uploadDocument(any(), eq(1L), any()))
                    .thenReturn(testDocument);

            mockMvc.perform(multipart("/api/v1/assignments/1/documents")
                            .file(file)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true));
        }

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should reject invalid file type")
        void shouldRejectInvalidFileType() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test.exe",
                    "application/x-msdownload",
                    "test content".getBytes()
            );

            mockMvc.perform(multipart("/api/v1/assignments/1/documents")
                            .file(file)
                            .with(csrf()))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Get Assignments Tests")
    class GetAssignmentsTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should get all assignments")
        void shouldGetAllAssignments() throws Exception {
            when(assignmentService.getAllAssignments(any()))
                    .thenReturn(Collections.singletonList(new AssignmentDTO(testAssignment, "")));

            mockMvc.perform(get("/api/v1/assignments")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray());
        }

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should get teacher assignments")
        void shouldGetTeacherAssignments() throws Exception {
            when(assignmentService.getAssignmentsByTeacher(eq(1L), any(), any(), any(), any()))
                    .thenReturn(Collections.singletonList(new AssignmentDTO(testAssignment, "")));

            mockMvc.perform(get("/api/v1/assignments/teacher/1")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray());
        }
    }

    @Nested
    @DisplayName("Grade Assignment Tests")
    class GradeAssignmentTests {

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should grade assignment successfully")
        void shouldGradeAssignment() throws Exception {
            GradeDTO gradeDTO = new GradeDTO();
            gradeDTO.setGrade(85.0);
            gradeDTO.setFeedback("Good work");

            when(assignmentService.gradeAssignment(eq(1L), any(), any(), eq(2L)))
                    .thenReturn(testAssignment);

            mockMvc.perform(patch("/api/v1/assignments/1/2/grade")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(gradeDTO)))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("Submit Assignment Tests")
    class SubmitAssignmentTests {

        @Test
        @WithMockUser(roles = "STUDENT")
        @DisplayName("Should submit assignment successfully")
        void shouldSubmitAssignment() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "document",
                    "submission.pdf",
                    "application/pdf",
                    "test content".getBytes()
            );

            StudentSubmission submission = StudentSubmission.builder()
                    .id(1L)
                    .student(studentUser)
                    .status(AssignmentStatus.SUBMITTED)
                    .build();

            when(assignmentService.submitAssignment(eq(1L), any(), any()))
                    .thenReturn(submission);

            mockMvc.perform(multipart("/api/v1/assignments/1/submit")
                            .file(file)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("Download Document Tests")
    class DownloadDocumentTests {

        @Test
        @WithMockUser(roles = "TEACHER")
        @DisplayName("Should download document successfully")
        void shouldDownloadDocument() throws Exception {
            ByteArrayResource resource = new ByteArrayResource("test content".getBytes());
            when(documentService.downloadDocument(eq(1L), any()))
                    .thenReturn(resource);

            mockMvc.perform(get("/api/v1/assignments/documents/1")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true));
        }
    }
}