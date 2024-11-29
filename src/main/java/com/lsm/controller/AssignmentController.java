package com.lsm.controller;

import com.lsm.model.DTOs.*;
import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.AssignmentDocument;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import com.lsm.service.AppUserService;
import com.lsm.service.AssignmentDocumentService;
import com.lsm.service.AssignmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Assignment Management", description = "APIs for managing student assignments")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final AppUserService appUserService;
    private final AssignmentDocumentService documentService;

    @Operation(
        summary = "Create a new assignment",
        description = "Allows teachers to create a new assignment for students"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Assignment created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_COORDINATOR')")
    @PostMapping("/createAssignment")
    public ResponseEntity<ApiResponse_<AssignmentDTO>> createAssignment(
            @Valid @RequestBody AssignmentRequestDTO assignmentRequest,
            Authentication authentication
    ) {
        try {
            AppUser currentUser = (AppUser) authentication.getPrincipal();
            log.info("Creating assignment for teacher: {}", currentUser.getUsername());

            Assignment assignment = assignmentService.createAssignment(assignmentRequest, currentUser.getId());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse_<>(
                            true,
                            "Assignment created successfully",
                            new AssignmentDTO(assignment, "Created successfully")
                    ));
        } catch (AccessDeniedException e) {
            log.error("Access denied while creating assignment: {}", e.getMessage());
            throw new SecurityException("Access denied: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error creating assignment: {}", e.getMessage());
            throw new RuntimeException("Error creating assignment");
        }
    }

    @Operation(
        summary = "Get student assignments",
        description = "Retrieve assignments for a specific student. Students can only access their own assignments."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Assignments retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse_<List<AssignmentDTO>>> getStudentAssignments(
            @Parameter(description = "ID of the student", required = true)
            @PathVariable @Positive Long studentId,
            Authentication authentication
    ) {
        try {
            AppUser currentUser = (AppUser) authentication.getPrincipal();
            // Additional security check to ensure students can only view their own assignments
            if (!currentUser.getId().equals(studentId)) {
                throw new AccessDeniedException("You can only view your own assignments");
            }
            List<AssignmentDTO> assignments = assignmentService
                .getAssignmentsByClass(studentId, currentUser);
            
            return ResponseEntity.ok(new ApiResponse_<>(
                true,
                "Assignments retrieved successfully",
                assignments
            ));
        } catch (AccessDeniedException e) {
            throw new SecurityException(e.getMessage());
        }
    }

    @Operation(
        summary = "Update an assignment",
        description = "Allows teachers to update their own assignments"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Assignment updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_COORDINATOR')")
    @PutMapping("/{assignmentId}")
    public ResponseEntity<ApiResponse_<AssignmentDTO>> updateAssignment(
            @Parameter(description = "ID of the assignment to update", required = true)
            @PathVariable @Positive Long assignmentId,
            @Valid @RequestBody AssignmentRequestDTO updateRequest,
            Authentication authentication
    ) {
        try {
            AppUser currentUser = (AppUser) authentication.getPrincipal();
            if (currentUser.getRole() == Role.ROLE_TEACHER && currentUser.equals(assignmentService.findById(assignmentId).getAssignedBy())) {
                throw new AccessDeniedException("Teacher can only update his/her own assignments");
            }
            Assignment updated = assignmentService.updateAssignment(assignmentId, updateRequest, currentUser.getId());
            
            return ResponseEntity.ok(new ApiResponse_<>(
                true,
                "Assignment updated successfully",
                new AssignmentDTO(updated, "Updated successfully")
            ));
        } catch (AccessDeniedException e) {
            throw new SecurityException(e.getMessage());
        }
    }

    @Operation(
            summary = "Update assignment status",
            description = "Update the status of an assignment. Students can only update to SUBMITTED status. " +
                    "Teachers can update their own assignments to GRADED status. " +
                    "Admins and Coordinators can update to any status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status update request"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PatchMapping("/{assignmentId}/status")
    public ResponseEntity<ApiResponse_<AssignmentDTO>> updateAssignmentStatus(
            @Parameter(description = "ID of the assignment to update", required = true)
            @PathVariable @Positive Long assignmentId,
            @Valid @RequestBody AssignmentStatusUpdateDTO statusUpdate,
            Authentication authentication
    ) {
        try {
            AppUser currentUser = (AppUser) authentication.getPrincipal();
            Assignment updated = assignmentService.updateAssignmentStatus(assignmentId, statusUpdate.getStatus(), currentUser);

            return ResponseEntity.ok(new ApiResponse_<>(
                    true,
                    "Assignment status updated successfully",
                    new AssignmentDTO(updated, "Status updated successfully")
            ));
        } catch (AccessDeniedException e) {
            throw new SecurityException(e.getMessage());
        }
    }

    @Operation(
            summary = "Get all assignments",
            description = "Retrieve all assignments in the system. Only accessible by administrators and coordinators."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignments retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COORDINATOR')")
    @GetMapping
    public ResponseEntity<ApiResponse_<List<AssignmentDTO>>> getAllAssignments(
            Authentication authentication
    ) {
        try {
            AppUser currentUser = (AppUser) authentication.getPrincipal();
            List<AssignmentDTO> assignments = assignmentService.getAllAssignments();

            return ResponseEntity.ok(new ApiResponse_<>(
                    true,
                    "All assignments retrieved successfully",
                    assignments
            ));
        } catch (Exception e) {
            throw new SecurityException(e.getMessage());
        }
    }

    @Operation(
        summary = "Delete an assignment",
        description = "Allows teachers to delete their own assignments"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Assignment deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_COORDINATOR')")
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<ApiResponse_<Void>> deleteAssignment(
            @Parameter(description = "ID of the assignment to delete", required = true)
            @PathVariable @Positive Long assignmentId,
            Principal principal
    ) {
        try {
            AppUser currentUser = appUserService.findByUsername(principal.getName());
            if (currentUser.getRole() == Role.ROLE_TEACHER && currentUser.equals(assignmentService.findById(assignmentId).getAssignedBy())) {
                throw new AccessDeniedException("Teacher can only delete his/her own assignments");
            }
            assignmentService.deleteAssignment(assignmentId, currentUser.getId());
            
            return ResponseEntity.ok(new ApiResponse_<>(
                true,
                "Assignment deleted successfully",
                null
            ));
        } catch (AccessDeniedException e) {
            throw new SecurityException(e.getMessage());
        }
    }

    @Operation(summary = "Upload assignment document")
    @PostMapping("/{assignmentId}/documents")
    public ResponseEntity<ApiResponse_<AssignmentDocumentDTO>> uploadDocument(
            @PathVariable @Positive Long assignmentId,
            @RequestParam("file") @NotNull MultipartFile file,
            @RequestParam(defaultValue = "false") boolean isTeacherUpload,
            Authentication authentication) {
        try {
            AppUser currentUser = (AppUser) authentication.getPrincipal();
            log.info("Uploading document for assignment: {}, user: {}", assignmentId, currentUser.getUsername());

            // Validate file size and type
            validateFile(file);

            AssignmentDocument document = documentService.uploadDocument(
                    file, assignmentId, currentUser, isTeacherUpload);

            return ResponseEntity.ok(new ApiResponse_<>(
                    true,
                    "Document uploaded successfully",
                    convertToDTO(document)
            ));
        } catch (IOException e) {
            log.error("Error uploading document: {}", e.getMessage());
            throw new RuntimeException("Error uploading document");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        if (file.getSize() > 10_000_000) { // 10MB limit
            throw new IllegalArgumentException("File size exceeds maximum limit");
        }
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedContentType(contentType)) {
            throw new IllegalArgumentException("Invalid file type");
        }
    }

    private boolean isAllowedContentType(String contentType) {
        return Arrays.asList(
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "text/plain"
        ).contains(contentType);
    }

    private AssignmentDocumentDTO convertToDTO(AssignmentDocument document) {
        return AssignmentDocumentDTO.builder()
                .id(document.getId())
                .fileName(document.getFileName())
                .fileType(document.getFileType())
                .fileSize(document.getFileSize())
                .uploadTime(document.getUploadTime())
                .uploadedByUsername(document.getUploadedBy().getUsername())
                .isTeacherUpload(document.isTeacherUpload())
                .build();
    }

    @Operation(
            summary = "Download assignment document",
            description = "Download a document associated with an assignment"
    )
    @GetMapping("/documents/{documentId}")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long documentId,
            Authentication authentication) {
        AppUser currentUser = (AppUser) authentication.getPrincipal();
        Resource resource = null;
        try {
            resource = documentService.downloadDocument(documentId, currentUser);
        } catch (IOException e) {
            throw new SecurityException(e.getMessage());
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @Operation(
            summary = "Grade an assignment",
            description = "Allows teachers to grade submitted assignments and provide feedback"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment graded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid grade data"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_COORDINATOR')")
    @PatchMapping("/{assignmentId}/grade")
    public ResponseEntity<ApiResponse_<AssignmentDTO>> gradeAssignment(
            @PathVariable @Positive Long assignmentId,
            @Valid @RequestBody GradeDTO gradeDTO,
            Authentication authentication
    ) {
        try {
            AppUser currentUser = (AppUser) authentication.getPrincipal();
            Assignment graded = assignmentService.gradeAssignment(assignmentId, gradeDTO, currentUser);

            return ResponseEntity.ok(new ApiResponse_<>(
                    true,
                    "Assignment graded successfully",
                    new AssignmentDTO(graded, "Graded successfully")
            ));
        } catch (AccessDeniedException e) {
            throw new SecurityException(e.getMessage());
        }
    }

    @Operation(
            summary = "Unsubmit an assignment",
            description = "Allows students to unsubmit their assignments if they haven't been graded yet"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment unsubmitted successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PatchMapping("/{assignmentId}/unsubmit")
    public ResponseEntity<ApiResponse_<AssignmentDTO>> unsubmitAssignment(
            @PathVariable @Positive Long assignmentId,
            Authentication authentication
    ) {
        try {
            AppUser currentUser = (AppUser) authentication.getPrincipal();
            Assignment unsubmitted = assignmentService.unsubmitAssignment(assignmentId, currentUser);

            return ResponseEntity.ok(new ApiResponse_<>(
                    true,
                    "Assignment unsubmitted successfully",
                    new AssignmentDTO(unsubmitted, "Unsubmitted successfully")
            ));
        } catch (AccessDeniedException e) {
            throw new SecurityException(e.getMessage());
        }
    }
}