package com.lsm.service;

import com.lsm.model.DTOs.AttendanceDTO;
import com.lsm.model.DTOs.AttendanceRequestDTO;
import com.lsm.model.entity.Attendance;
import com.lsm.model.entity.base.AppUser;
import com.lsm.repository.AttendanceRepository;
import com.lsm.repository.AppUserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
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
    public void markAttendance(AttendanceRequestDTO attendanceRequest) {
        AppUser student = appUserRepository.findById(attendanceRequest.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Student with ID %d not found.", attendanceRequest.getStudentId())
                ));

        Attendance attendance = mapToAttendanceEntity(attendanceRequest, student);
        attendanceRepository.save(attendance);
    }

    /**
     * Retrieves attendance records for a specific student.
     * Students can only access their own records, while teachers and admins can access any student's records.
     *
     * @param studentId the ID of the student whose attendance records are requested.
     * @return List of attendance records as DTOs.
     * @throws SecurityException if a student tries to access another student's records.
     */
    public List<AttendanceDTO> getAttendanceByStudentId(Long studentId) {
        AppUser currentUser = getAuthenticatedUser();
        validateAccessPermissions(currentUser, studentId);

        return attendanceRepository.findByStudentId(studentId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Creates an Attendance entity from the request DTO and student entity.
     *
     * @param request the attendance request details.
     * @param student the student associated with the attendance record.
     * @return the constructed Attendance entity.
     */
    private Attendance mapToAttendanceEntity(AttendanceRequestDTO request, AppUser student) {
        return Attendance.builder()
                .student(student)
                .date(request.getDate())
                .status(request.getStatus())
                .comment(request.getComment())
                .classId(request.getClassId())
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
        AppUser user = appUserRepository.findByUsername(username);
        if(user == null)
            throw new UsernameNotFoundException("User not found");
        return user;
    }

    /**
     * Converts an Attendance entity to its DTO representation.
     *
     * @param attendance the Attendance entity to be converted.
     * @return the DTO representation of the attendance.
     */
    private AttendanceDTO convertToDTO(Attendance attendance) {
        return AttendanceDTO.builder()
                .id(attendance.getId())
                .studentId(attendance.getStudent().getId())
                .studentName(attendance.getStudent().getStudentDetails().getName())
                .studentSurname(attendance.getStudent().getStudentDetails().getSurname())
                .date(attendance.getDate())
                .status(attendance.getStatus())
                .comment(attendance.getComment())
                .classId(attendance.getClassId())
                .build();
    }
}
