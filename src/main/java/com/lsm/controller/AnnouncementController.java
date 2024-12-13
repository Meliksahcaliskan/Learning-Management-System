package com.lsm.controller;

import com.lsm.model.DTOs.AnnouncementDTO;
import com.lsm.model.entity.base.AppUser;
import com.lsm.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/announcements")
@RequiredArgsConstructor
@Validated
@Tag(name = "Announcement Management", description = "APIs for managing announcements")
@SecurityRequirement(name = "bearerAuth")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @Operation(summary = "Create a new announcement", description = "Only teachers, admins, and coordinators can create announcements")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Announcement created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_COORDINATOR')")
    @PostMapping
    public ResponseEntity<ApiResponse_<AnnouncementDTO>> createAnnouncement(
            @Valid @RequestBody AnnouncementDTO announcementDTO,
            Authentication authentication) {
        try {
            AppUser loggedInUser = (AppUser) authentication.getPrincipal();
            AnnouncementDTO createdAnnouncement = announcementService.createAnnouncement(loggedInUser, announcementDTO);
            ApiResponse_<AnnouncementDTO> response = new ApiResponse_<>(
                    true,
                    "Announcement created successfully",
                    createdAnnouncement
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (AccessDeniedException e) {
            log.error("Access denied in createAnnouncement: {}", e.getMessage());
            return httpError(HttpStatus.FORBIDDEN, "Access denied: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error in createAnnouncement: {}", e.getMessage());
            return httpError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
        }
    }

    @Operation(summary = "Get announcements by class", description = "Retrieve all announcements for a specific class")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Announcements retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Class not found")
    })
    @GetMapping("/class/{classId}")
    public ResponseEntity<ApiResponse_<List<AnnouncementDTO>>> getAnnouncementsByClass(
            @PathVariable Long classId,
            Authentication authentication) {
        try {
            AppUser loggedInUser = (AppUser) authentication.getPrincipal();
            List<AnnouncementDTO> announcements = announcementService.getAnnouncementsByClassId(loggedInUser, classId);
            ApiResponse_<List<AnnouncementDTO>> response = new ApiResponse_<>(
                    true,
                    "Announcements retrieved successfully",
                    announcements
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving announcements: {}", e.getMessage());
            return httpError(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving announcements: " + e.getMessage());
        }
    }

    @Operation(summary = "Update an announcement", description = "Only teachers, admins, and coordinators can update announcements")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Announcement updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Announcement not found")
    })
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_COORDINATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse_<AnnouncementDTO>> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementDTO announcementDTO,
            Authentication authentication) {
        try {
            AppUser loggedInUser = (AppUser) authentication.getPrincipal();
            AnnouncementDTO updatedAnnouncement = announcementService.updateAnnouncement(loggedInUser, id, announcementDTO);
            ApiResponse_<AnnouncementDTO> response = new ApiResponse_<>(
                    true,
                    "Announcement updated successfully",
                    updatedAnnouncement
            );
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException e) {
            log.error("Access denied in updateAnnouncement: {}", e.getMessage());
            return httpError(HttpStatus.FORBIDDEN, "Access denied: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error in updateAnnouncement: {}", e.getMessage());
            return httpError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
        }
    }

    @Operation(summary = "Delete an announcement", description = "Only teachers, admins, and coordinators can delete announcements")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Announcement deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Announcement not found")
    })
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_COORDINATOR')")
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<ApiResponse_<Void>> deleteAnnouncement(@PathVariable Long assignmentId, Authentication authentication) {
        try {
            AppUser loggedInUser = (AppUser) authentication.getPrincipal();
            announcementService.deleteAnnouncement(loggedInUser, assignmentId);
            ApiResponse_<Void> response = new ApiResponse_<>(
                    true,
                    "Announcement deleted successfully",
                    null
            );
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            return httpError(HttpStatus.FORBIDDEN, "Access denied: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            return httpError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + e.getMessage());
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