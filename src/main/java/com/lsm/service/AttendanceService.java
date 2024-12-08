package com.lsm.service;

import com.lsm.model.DTOs.AttendanceDTO;
import com.lsm.model.DTOs.AttendanceRequestDTO;
import com.lsm.model.DTOs.AttendanceStatsDTO;
import com.lsm.model.entity.Attendance;
import com.lsm.model.entity.ClassEntity;
import com.lsm.model.entity.Course;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.AttendanceStatus;
import com.lsm.model.entity.enums.Role;
import com.lsm.repository.AttendanceRepository;
import com.lsm.repository.AppUserRepository;
import com.lsm.repository.ClassEntityRepository;
import com.lsm.repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final AppUserRepository appUserRepository;
    private final CourseRepository courseRepository;
    private final ClassEntityRepository classEntityRepository;
    private final AppUserService appUserService;

    public AttendanceService(AttendanceRepository attendanceRepository, AppUserRepository appUserRepository, CourseRepository courseRepository, ClassEntityRepository classRepository, AppUserService appUserService) {
        this.attendanceRepository = attendanceRepository;
        this.appUserRepository = appUserRepository;
        this.courseRepository = courseRepository;
        this.classEntityRepository = classRepository;
        this.appUserService = appUserService;
    }

    @Transactional
    public Attendance markAttendance(AppUser loggedInUser, AttendanceRequestDTO attendanceRequest) throws AccessDeniedException {
        if (loggedInUser.getRole().equals(Role.ROLE_STUDENT))
            throw new AccessDeniedException("Students can't mark attendance");
        if (loggedInUser .getRole().equals(Role.ROLE_TEACHER)) {
            AppUser user = appUserService.getCurrentUserWithDetails(loggedInUser.getId());
            boolean found = user.getTeacherDetails().getClasses().stream()
                    .flatMap(classEntity -> classEntity.getCourses().stream())
                    .anyMatch(courseEntity -> courseEntity.getId().equals(attendanceRequest.getCourseId()));

            if (!found) {
                throw new AccessDeniedException("Teachers can only mark attendance to their courses");
            }
        }
        AppUser student = appUserRepository.findById(attendanceRequest.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Student with ID %d not found.", attendanceRequest.getStudentId())
                ));

        Attendance attendance = mapToAttendanceEntity(attendanceRequest, student);
        return attendanceRepository.save(attendance);
    }

    // TODO: fix it like the markAttendance
    @Transactional
    public void markBulkAttendance(AppUser loggedInUser, List<AttendanceRequestDTO> attendanceRequests)
            throws AccessDeniedException {
        if (loggedInUser.getRole().equals(Role.ROLE_STUDENT))
            throw new AccessDeniedException("Students can't mark attendance");
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
    public List<AttendanceDTO> getAttendanceByStudentId(AppUser loggedInUser , Long studentId, LocalDate startDate, LocalDate endDate) {
        validateAccessPermissions(loggedInUser , studentId);

        return attendanceRepository.findByStudentId(studentId)
                .stream()
                .filter(attendance -> startDate == null || endDate == null || !attendance.getDate().isBefore(startDate) && !attendance.getDate().isAfter(endDate))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<AttendanceStatsDTO> getAttendanceStatsByStudent(AppUser loggedInUser, Long studentId, Long classId) {
        validateAccessPermissions(loggedInUser, studentId);

        AppUser student = appUserRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        ClassEntity classEntity = classEntityRepository.getClassEntityById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));

        List<Attendance> attendances;
        if (classId != null) {
            // Get attendances for specific class
            attendances = attendanceRepository.findByStudentIdAndClassId(studentId, classId);
        } else {
            // Get all attendances for the student
            attendances = attendanceRepository.findByStudentId(studentId);
        }

        // Group attendances by course
        return attendances.stream()
                .collect(Collectors.groupingBy(Attendance::getCourseId))
                .entrySet().stream()
                .map(entry -> {
                    Long courseId = entry.getKey();
                    List<Attendance> courseAttendances = entry.getValue();

                    Course course = courseRepository.findById(courseId)
                            .orElseThrow(() -> new IllegalArgumentException("Course not found"));

                    long totalClasses = courseAttendances.size();
                    long presentCount = courseAttendances.stream()
                            .filter(a -> AttendanceStatus.PRESENT.equals(a.getStatus()))
                            .count();
                    long absentCount = courseAttendances.stream()
                            .filter(a -> AttendanceStatus.ABSENT.equals(a.getStatus()))
                            .count();
                    long lateCount = courseAttendances.stream()
                            .filter(a -> AttendanceStatus.EXCUSED.equals(a.getStatus()))
                            .count();

                    double attendancePercentage = totalClasses > 0
                            ? (presentCount + lateCount) * 100.0 / totalClasses
                            : 0;

                    return AttendanceStatsDTO.builder()
                            .studentId(studentId)
                            .studentName(student.getName())
                            .classId(classEntity.getId())
                            .className(classEntity.getName())
                            .courseId(courseId)
                            .courseName(course.getName())
                            .totalClasses(totalClasses)
                            .presentCount(presentCount)
                            .absentCount(absentCount)
                            .lateCount(lateCount)
                            .attendancePercentage(Math.round(attendancePercentage * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<AttendanceStatsDTO> getAttendanceStatsByCourse(AppUser loggedInUser, Long courseId, Long classId)
            throws AccessDeniedException {
        if (loggedInUser.getRole().equals(Role.ROLE_STUDENT))
            throw new AccessDeniedException("Students can't view attendance stats of the course.");
        if (loggedInUser.getRole().equals(Role.ROLE_TEACHER)) {

        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        ClassEntity classEntity = classEntityRepository.getClassEntityById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));

        List<Attendance> attendances;
        if (classId != null) {
            // Get attendances for specific class in the course
            attendances = attendanceRepository.findByCourseIdAndClassId(courseId, classId);
        } else {
            // Get all attendances for the course
            attendances = attendanceRepository.findByCourseId(courseId);
        }

        // Group attendances by student
        return attendances.stream()
                .collect(Collectors.groupingBy(attendance -> attendance.getStudent().getId()))
                .entrySet().stream()
                .map(entry -> {
                    Long studentId = entry.getKey();
                    List<Attendance> studentAttendances = entry.getValue();

                    AppUser student = appUserRepository.findById(studentId)
                            .orElseThrow(() -> new IllegalArgumentException("Student not found"));

                    long totalClasses = studentAttendances.size();
                    long presentCount = studentAttendances.stream()
                            .filter(a -> AttendanceStatus.PRESENT.equals(a.getStatus()))
                            .count();
                    long absentCount = studentAttendances.stream()
                            .filter(a -> AttendanceStatus.ABSENT.equals(a.getStatus()))
                            .count();
                    long lateCount = studentAttendances.stream()
                            .filter(a -> AttendanceStatus.EXCUSED.equals(a.getStatus()))
                            .count();

                    double attendancePercentage = totalClasses > 0
                            ? (presentCount + lateCount) * 100.0 / totalClasses
                            : 0;

                    return AttendanceStatsDTO.builder()
                            .studentId(studentId)
                            .studentName(student.getName())
                            .classId(classEntity.getId())
                            .className(classEntity.getName())
                            .courseId(courseId)
                            .courseName(course.getName())
                            .totalClasses(totalClasses)
                            .presentCount(presentCount)
                            .absentCount(absentCount)
                            .lateCount(lateCount)
                            .attendancePercentage(Math.round(attendancePercentage * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());
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
        if (Role.ROLE_STUDENT.equals(currentUser.getRole()) && !currentUser.getId().equals(requestedStudentId)) {
            throw new SecurityException("Students can only access their own attendance records.");
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
                .courseId(attendance.getCourseId())
                .build();
    }
}
