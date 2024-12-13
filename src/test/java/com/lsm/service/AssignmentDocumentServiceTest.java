package com.lsm.service;

import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.AssignmentDocument;
import com.lsm.model.entity.ClassEntity;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import com.lsm.repository.AssignmentDocumentRepository;
import com.lsm.repository.AssignmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentDocumentServiceTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private AssignmentDocumentRepository documentRepository;

    @InjectMocks
    private AssignmentDocumentService documentService;

    @TempDir
    Path tempDir;

    private AppUser teacherUser;
    private AppUser studentUser;
    private AppUser adminUser;
    private Assignment assignment;
    private AssignmentDocument document;
    private MockMultipartFile mockFile;
    private ClassEntity classEntity;

    @BeforeEach
    void setUp() {
        // Set up users
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

        // Set up class entity
        classEntity = ClassEntity.builder()
                .id(1L)
                .name("Test Class")
                .students(new HashSet<>(Set.of(studentUser)))
                .build();

        // Set up assignment
        assignment = Assignment.builder()
                .id(1L)
                .assignedBy(teacherUser)
                .classEntity(classEntity)
                .build();

        // Set up document
        document = AssignmentDocument.builder()
                .id(1L)
                .fileName("test.pdf")
                .filePath(tempDir.resolve("test.pdf").toString())
                .fileType("application/pdf")
                .fileSize(1024L)
                .uploadTime(LocalDateTime.now())
                .uploadedBy(teacherUser)
                .assignment(assignment)
                .build();

        // Set up mock file
        mockFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        // Set upload directory
        ReflectionTestUtils.setField(documentService, "uploadDir", tempDir.toString());
    }

    @Nested
    @DisplayName("Upload Document Tests")
    class UploadDocumentTests {

        @Test
        @DisplayName("Should successfully upload document as teacher")
        void shouldUploadDocumentSuccessfully() throws Exception {
            // Arrange
            when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
            when(documentRepository.save(any(AssignmentDocument.class))).thenReturn(document);

            // Act
            AssignmentDocument result = documentService.uploadDocument(mockFile, 1L, teacherUser);

            // Assert
            assertNotNull(result);
            assertEquals(document.getFileName(), result.getFileName());
            verify(documentRepository).save(any(AssignmentDocument.class));
            verify(assignmentRepository).save(any(Assignment.class));
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when student tries to upload")
        void shouldThrowAccessDeniedForStudent() {
            // Act & Assert
            assertThrows(AccessDeniedException.class,
                    () -> documentService.uploadDocument(mockFile, 1L, studentUser));
        }

        @Test
        @DisplayName("Should replace existing document when uploading new one")
        void shouldReplaceExistingDocument() throws Exception {
            // Arrange
            assignment.setTeacherDocument(document);
            when(assignmentRepository.findById(1L)).thenReturn(Optional.of(assignment));
            when(documentRepository.save(any(AssignmentDocument.class))).thenReturn(document);

            // Create a real file to test deletion
            Files.write(Path.of(document.getFilePath()), "test content".getBytes());

            // Act
            AssignmentDocument result = documentService.uploadDocument(mockFile, 1L, teacherUser);

            // Assert
            assertNotNull(result);
            verify(documentRepository).delete(any(AssignmentDocument.class));
        }
    }

    @Nested
    @DisplayName("Download Document Tests")
    class DownloadDocumentTests {

        @Test
        @DisplayName("Should allow teacher to download their document")
        void shouldAllowTeacherToDownloadOwnDocument() throws Exception {
            // Arrange
            when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
            Files.write(Path.of(document.getFilePath()), "test content".getBytes());

            // Act
            Resource result = documentService.downloadDocument(1L, teacherUser);

            // Assert
            assertTrue(result.exists());
            assertTrue(result.isReadable());
        }

        @Test
        @DisplayName("Should allow student to download their class document")
        void shouldAllowStudentToDownloadClassDocument() throws Exception {
            // Arrange
            when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
            Files.write(Path.of(document.getFilePath()), "test content".getBytes());

            // Act
            Resource result = documentService.downloadDocument(1L, studentUser);

            // Assert
            assertTrue(result.exists());
            assertTrue(result.isReadable());
        }

        @Test
        @DisplayName("Should throw AccessDeniedException for unauthorized access")
        void shouldThrowAccessDeniedForUnauthorizedAccess() {
            // Arrange
            AppUser unauthorizedStudent = AppUser.builder()
                    .id(4L)
                    .role(Role.ROLE_STUDENT)
                    .build();
            when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

            // Act & Assert
            assertThrows(AccessDeniedException.class,
                    () -> documentService.downloadDocument(1L, unauthorizedStudent));
        }
    }

    @Nested
    @DisplayName("Delete Document Tests")
    class DeleteDocumentTests {

        @Test
        @DisplayName("Should successfully delete document")
        void shouldDeleteDocumentSuccessfully() throws Exception {
            // Arrange
            when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
            Files.write(Path.of(document.getFilePath()), "test content".getBytes());

            // Act
            documentService.deleteDocument(1L, teacherUser);

            // Assert
            verify(documentRepository).delete(document);
            assertFalse(Files.exists(Path.of(document.getFilePath())));
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when document not found")
        void shouldThrowEntityNotFoundForNonexistentDocument() {
            // Arrange
            when(documentRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(EntityNotFoundException.class,
                    () -> documentService.deleteDocument(1L, teacherUser));
        }

        @Test
        @DisplayName("Should handle missing file during deletion")
        void shouldHandleMissingFileGracefully() throws Exception {
            // Arrange
            when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

            // Act
            documentService.deleteDocument(1L, teacherUser);

            // Assert
            verify(documentRepository).delete(document);
        }
    }
}