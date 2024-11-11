package com.lsm.controller;

import com.lsm.model.DTOs.AssignmentDTO;
import com.lsm.model.DTOs.AssignmentRequestDTO;
import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.base.AppUser;
import com.lsm.service.AppUserService;
import com.lsm.service.AssignmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
@Validated
@Tag(name = "Assignment Management", description = "APIs for managing student assignments")
@SecurityRequirement(name = "bearerAuth")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final AppUserService appUserService;

    @Operation(
        summary = "Create a new assignment",
        description = "Allows teachers to create a new assignment for students"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Assignment created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse_<AssignmentDTO>> createAssignment(
            @Valid @RequestBody AssignmentRequestDTO assignmentRequest,
            Principal principal
    ) {
        try {
            Long teacherId = appUserService.findByUsername(principal.getName()).getId();
            Assignment assignment = assignmentService.createAssignment(assignmentRequest, teacherId);
            
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse_<>(
                    true,
                    "Assignment created successfully",
                    new AssignmentDTO(assignment, "Created successfully")
                ));
        } catch (AccessDeniedException e) {
            throw new SecurityException(e.getMessage());
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
            List<AssignmentDTO> assignments = assignmentService
                .displayAssignmentsForStudent(studentId, currentUser.getId());
            
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
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @PutMapping("/{assignmentId}")
    public ResponseEntity<ApiResponse_<AssignmentDTO>> updateAssignment(
            @Parameter(description = "ID of the assignment to update", required = true)
            @PathVariable @Positive Long assignmentId,
            @Valid @RequestBody AssignmentRequestDTO updateRequest,
            Authentication authentication
    ) {
        try {
            AppUser currentUser = (AppUser) authentication.getPrincipal();
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
        summary = "Delete an assignment",
        description = "Allows teachers to delete their own assignments"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Assignment deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
        @ApiResponse(responseCode = "404", description = "Assignment not found")
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<ApiResponse_<Void>> deleteAssignment(
            @Parameter(description = "ID of the assignment to delete", required = true)
            @PathVariable @Positive Long assignmentId,
            Principal principal
    ) {
        try {
            Long teacherId = appUserService.findByUsername(principal.getName()).getId();
            assignmentService.deleteAssignment(assignmentId, teacherId);
            
            return ResponseEntity.ok(new ApiResponse_<>(
                true,
                "Assignment deleted successfully",
                null
            ));
        } catch (AccessDeniedException e) {
            throw new SecurityException(e.getMessage());
        }
    }
}