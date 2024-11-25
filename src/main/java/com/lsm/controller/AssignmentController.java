package com.lsm.controller;

import com.lsm.model.DTOs.AssignmentDTO;
import com.lsm.model.DTOs.AssignmentRequestDTO;
import com.lsm.model.DTOs.AssignmentStatusUpdateDTO;
import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
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
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN')")
    @PostMapping("/createAssignment")
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
}