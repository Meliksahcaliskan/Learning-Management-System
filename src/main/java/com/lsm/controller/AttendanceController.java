package com.lsm.controller;

import com.lsm.model.DTOs.AttendanceDTO;
import com.lsm.model.DTOs.AttendanceStatsDTO;
import com.lsm.model.DTOs.AttendanceRequestDTO;
import com.lsm.model.entity.Attendance;
import com.lsm.model.entity.base.AppUser;
import com.lsm.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Validated
@Tag(name = "Attendance Management", description = "APIs for managing student attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Operation(
            summary = "Mark student attendance",
            description = "Allows teachers and administrators to mark attendance for a student"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Attendance marked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_COORDINATOR')")
    @PostMapping("/mark")
    public ResponseEntity<ApiResponse_<AttendanceDTO>> markAttendance(
            @Valid @RequestBody AttendanceRequestDTO attendanceRequest
    ) {
        Attendance attendance = attendanceService.markAttendance(attendanceRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse_<>(
                        true,
                        "Attendance marked successfully",
                        new AttendanceDTO(attendance, "")
                ));
    }

    @Operation(
            summary = "Get student attendance records",
            description = "Retrieve attendance records for a specific student. Students can only access their own records."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Records retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_COORDINATOR')")
    @GetMapping("/{studentId}")
    public ResponseEntity<ApiResponse_<List<AttendanceDTO>>> getAttendanceByStudentId(
            @Parameter(description = "ID of the student", required = true)
            @PathVariable @Positive Long studentId,
            @Parameter(description = "Start date (YYYY-MM-DD)")
            @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "End date (YYYY-MM-DD)")
            @RequestParam(required = false) LocalDate endDate,
            Authentication authentication
    ) {
        AppUser loggedInUser = (AppUser) authentication.getPrincipal();
        List<AttendanceDTO> attendanceRecords = attendanceService.getAttendanceByStudentId(loggedInUser, studentId, startDate, endDate);
        return ResponseEntity.ok(new ApiResponse_<>(
                true,
                "Attendance records retrieved successfully",
                attendanceRecords
        ));
    }

    @Operation(
            summary = "Get attendance statistics",
            description = "Retrieve attendance statistics for a specific student or class"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Student/Class not found")
    })
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_COORDINATOR')")
    @GetMapping("/stats/{studentId}")
    public ResponseEntity<ApiResponse_<AttendanceStatsDTO>> getAttendanceStats(
            @Parameter(description = "ID of the student", required = true)
            @PathVariable @Positive Long studentId,
            @Parameter(description = "ID of the class")
            @RequestParam(required = false) Long classId
    ) {
        AttendanceStatsDTO stats = attendanceService.getAttendanceStats(studentId, classId);
        return ResponseEntity.ok(new ApiResponse_<>(
                true,
                "Attendance statistics retrieved successfully",
                stats
        ));
    }

    @Operation(
            summary = "Bulk mark attendance",
            description = "Mark attendance for multiple students at once"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Attendance marked successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions")
    })
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN', 'ROLE_COORDINATOR')")
    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse_<Integer>> markBulkAttendance(
            @Valid @RequestBody List<AttendanceRequestDTO> attendanceRequests
    ) {
        attendanceService.markBulkAttendance(attendanceRequests);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse_<>(
                        true,
                        "Bulk attendance marked successfully",
                        attendanceRequests.size()
                ));
    }
}