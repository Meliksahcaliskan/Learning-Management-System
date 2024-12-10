package com.lsm.controller;

import com.lsm.model.DTOs.CourseDTO;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import com.lsm.service.CourseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "Course Management", description = "APIs for managing courses")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID", description = "Retrieve a specific course by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COORDINATOR', 'ROLE_TEACHER', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse_<CourseDTO>> getCourseById(
            @Parameter(description = "ID of the course", required = true)
            @PathVariable @Positive Long id
    ) {
        try {
            log.info("Retrieving course with ID: {}", id);
            return ResponseEntity.ok(new ApiResponse_<>(
                    true,
                    "Course retrieved successfully",
                    courseService.getCourseById(id)
            ));
        } catch (EntityNotFoundException e) {
            log.error("Course not found with ID {}: {}", id, e.getMessage());
            return httpError(HttpStatus.NOT_FOUND, "Course not found: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving course with ID {}: {}", id, e.getMessage());
            return httpError(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving course: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Get all courses", description = "Retrieve all courses in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COORDINATOR')")
    public ResponseEntity<ApiResponse_<List<CourseDTO>>> getAllCourses() {
        try {
            log.info("Retrieving all courses");
            return ResponseEntity.ok(new ApiResponse_<>(
                    true,
                    "All courses retrieved successfully",
                    courseService.getAllCourses()
            ));
        } catch (Exception e) {
            log.error("Error retrieving all courses: {}", e.getMessage());
            return httpError(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving courses: " + e.getMessage());
        }
    }

    @GetMapping("/class/{classId}")
    @Operation(summary = "Get courses by class ID", description = "Retrieve all courses for a specific class")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Class not found")
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COORDINATOR', 'ROLE_TEACHER', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse_<List<CourseDTO>>> getCoursesByClassId(
            @Parameter(description = "ID of the class", required = true)
            @PathVariable @Positive Long classId
    ) {
        try {
            log.info("Retrieving courses for class ID: {}", classId);
            return ResponseEntity.ok(new ApiResponse_<>(
                    true,
                    "Courses for class retrieved successfully",
                    courseService.getCoursesByClassId(classId)
            ));
        } catch (EntityNotFoundException e) {
            log.error("Class not found with ID {}: {}", classId, e.getMessage());
            return httpError(HttpStatus.NOT_FOUND, "Class not found: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving courses for class {}: {}", classId, e.getMessage());
            return httpError(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving courses: " + e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get courses by student", description = "Retrieve all courses a student is enrolled in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COORDINATOR', 'ROLE_TEACHER', 'ROLE_STUDENT')")
    public ResponseEntity<ApiResponse_<List<CourseDTO>>> getCoursesByStudent(
            @Parameter(description = "ID of the student", required = true)
            @PathVariable @Positive Long studentId,
            Authentication authentication
    ) {
        try {
            AppUser currentUser = (AppUser) authentication.getPrincipal();
            log.info("Retrieving courses for student ID: {}", studentId);

            // Students can only view their own courses
            if (currentUser.getRole() == Role.ROLE_STUDENT && !currentUser.getId().equals(studentId)) {
                throw new AccessDeniedException("Students can only view their own courses");
            }

            return ResponseEntity.ok(new ApiResponse_<>(
                    true,
                    "Student courses retrieved successfully",
                    courseService.getCoursesByStudent(studentId)
            ));
        } catch (EntityNotFoundException e) {
            log.error("Student not found with ID {}: {}", studentId, e.getMessage());
            return httpError(HttpStatus.NOT_FOUND, "Student not found: " + e.getMessage());
        } catch (AccessDeniedException e) {
            log.error("Access denied for student courses: {}", e.getMessage());
            return httpError(HttpStatus.FORBIDDEN, "Access denied: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving courses for student {}: {}", studentId, e.getMessage());
            return httpError(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving courses: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create a new course", description = "Create a new course in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Course created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COORDINATOR')")
    public ResponseEntity<ApiResponse_<CourseDTO>> createCourse(
            @Valid @RequestBody CourseDTO courseDTO
    ) {
        try {
            log.info("Creating new course: {}", courseDTO.getName());
            CourseDTO createdCourse = courseService.createCourse(courseDTO);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ApiResponse_<>(
                            true,
                            "Course created successfully",
                            createdCourse
                    ));
        } catch (Exception e) {
            log.error("Error creating course: {}", e.getMessage());
            return httpError(HttpStatus.BAD_REQUEST, "Error creating course: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a course", description = "Update an existing course in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COORDINATOR')")
    public ResponseEntity<ApiResponse_<CourseDTO>> updateCourse(
            @Parameter(description = "ID of the course to update", required = true)
            @PathVariable @Positive Long id,
            @Valid @RequestBody CourseDTO courseDTO
    ) {
        try {
            log.info("Updating course with ID: {}", id);
            return ResponseEntity.ok(new ApiResponse_<>(
                    true,
                    "Course updated successfully",
                    courseService.updateCourse(id, courseDTO)
            ));
        } catch (EntityNotFoundException e) {
            log.error("Course not found with ID {}: {}", id, e.getMessage());
            return httpError(HttpStatus.NOT_FOUND, "Course not found: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error updating course {}: {}", id, e.getMessage());
            return httpError(HttpStatus.BAD_REQUEST, "Error updating course: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a course", description = "Delete an existing course from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_COORDINATOR')")
    public ResponseEntity<ApiResponse_<Void>> deleteCourse(
            @Parameter(description = "ID of the course to delete", required = true)
            @PathVariable @Positive Long id
    ) {
        try {
            log.info("Deleting course with ID: {}", id);
            courseService.deleteCourse(id);
            return ResponseEntity.ok(new ApiResponse_<>(
                    true,
                    "Course deleted successfully",
                    null
            ));
        } catch (EntityNotFoundException e) {
            log.error("Course not found with ID {}: {}", id, e.getMessage());
            return httpError(HttpStatus.NOT_FOUND, "Course not found: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting course {}: {}", id, e.getMessage());
            return httpError(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting course: " + e.getMessage());
        }
    }

    private static <T> ResponseEntity<ApiResponse_<T>> httpError(HttpStatus status, String message) {
        return ResponseEntity
                .status(status)
                .body(new ApiResponse_<>(
                        false,
                        message,
                        null
                ));
    }
}