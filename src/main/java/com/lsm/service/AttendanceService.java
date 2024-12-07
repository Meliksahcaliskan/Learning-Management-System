package com.lsm.service;

import com.lsm.model.DTOs.AttendanceDTO;
import com.lsm.model.DTOs.AttendanceRequestDTO;
import com.lsm.model.DTOs.AttendanceStatsDTO;
import com.lsm.model.entity.Attendance;
import com.lsm.model.entity.base.AppUser;
import com.lsm.repository.AttendanceRepository;
import com.lsm.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private static final String STUDENT_ROLE = "ROLE_STUDENT";
    private static final String ACCESS_DENIED_ERROR = "Students can only access their own attendance records.";

    private final AttendanceRepository attendanceRepository;
    private final AppUserRepository appUserRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, AppUserRepository appUserRepository) {
        this.attendanceRepository = attendanceRepository;
        this.appUserRepository = appUserRepository;
    }

    /**
     * Marks attendance for a student based on the provided attendance request.
     * 
     * @param attendanceRequest the attendance details.
     * @throws IllegalArgumentException if the student is not found.
     */
    @Transactional
    public Attendance markAttendance(AttendanceRequestDTO attendanceRequest) {
        AppUser student = appUserRepository.findById(attendanceRequest.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Student with ID %d not found.", attendanceRequest.getStudentId())
                ));

        Attendance attendance = mapToAttendanceEntity(attendanceRequest, student);
        return attendanceRepository.save(attendance);
    }

    @Transactional
    public void markBulkAttendance(List<AttendanceRequestDTO> attendanceRequests) {
        for(AttendanceRequestDTO attendanceRequest : attendanceRequests) {
            AppUser student = appUserRepository.findById(attendanceRequest.getStudentId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            String.format("Student with ID %d not found.", attendanceRequest.getStudentId())
                    ));

            Attendance attendance = mapToAttendanceEntity(attendanceRequest, student);
            attendanceRepository.save(attendance);
        }
    }

    @Transactional
    public List<AttendanceDTO> getAttendanceByStudentId(AppUser  loggedInUser , Long studentId, LocalDate startDate, LocalDate endDate) {
        validateAccessPermissions(loggedInUser , studentId);

        return attendanceRepository.findByStudentId(studentId)
                .stream()
                .filter(attendance -> startDate == null || endDate == null || !attendance.getDate().isBefore(startDate) && !attendance.getDate().isAfter(endDate))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // TODO: this is a placeholder
    @Transactional
    public AttendanceStatsDTO getAttendanceStats(Long studentId, Long classId) {
        AppUser currentUser = getAuthenticatedUser();
        validateAccessPermissions(currentUser, studentId);

        return AttendanceStatsDTO.builder()
                .studentId(studentId)
                .classId(classId)
                .build();
    }

    private Attendance mapToAttendanceEntity(AttendanceRequestDTO request, AppUser student) {
        return Attendance.builder()
                .student(student)
                .date(request.getDate())
                .status(request.getStatus())
                .comment(request.getComment())
                .classId(request.getClassId())
                .courseId(request.getCourseId())
                .build();
    }

    /**
     * Validates whether the current user has permission to access the requested attendance records.
     *
     * @param currentUser       the user currently authenticated.
     * @param requestedStudentId the ID of the student whose records are requested.
     */
    private void validateAccessPermissions(AppUser currentUser, Long requestedStudentId) {
        if (STUDENT_ROLE.equals(currentUser.getRole().name()) && !currentUser.getId().equals(requestedStudentId)) {
            throw new SecurityException(ACCESS_DENIED_ERROR);
        }
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * @return the authenticated AppUser.
     * @throws UsernameNotFoundException if the user is not found in the database.
     */
    private AppUser getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<AppUser> user = appUserRepository.findByUsername(username);
        if(user.isEmpty())
            throw new UsernameNotFoundException("User not found");
        return user.get();
    }

    /**
     * Converts an Attendance entity to its DTO representation.
     *
     * @param attendance the Attendance entity to be converted.
     * @return the DTO representation of the attendance.
     */
    private AttendanceDTO convertToDTO(Attendance attendance) {
        return AttendanceDTO.builder()
                .attendanceId(attendance.getId())
                .studentId(attendance.getStudent().getId())
                .studentName(attendance.getStudent().getName())
                .studentSurname(attendance.getStudent().getSurname())
                .date(attendance.getDate())
                .status(attendance.getStatus())
                .comment(attendance.getComment())
                .classId(attendance.getClassId())
                .build();
    }
}
