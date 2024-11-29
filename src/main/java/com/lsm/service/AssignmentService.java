package com.lsm.service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.lsm.model.DTOs.GradeDTO;
import com.lsm.model.entity.ClassEntity;
import com.lsm.model.entity.Course;
import com.lsm.model.entity.enums.AssignmentStatus;
import com.lsm.repository.ClassEntityRepository;
import com.lsm.repository.CourseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import com.lsm.model.DTOs.AssignmentDTO;
import com.lsm.model.DTOs.AssignmentRequestDTO;
import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import com.lsm.repository.AppUserRepository;
import com.lsm.repository.AssignmentRepository;

import jakarta.transaction.Transactional;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.validation.annotation.Validated;

@Service
@Slf4j
@Validated
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AppUserRepository appUserRepository;
    private final ClassEntityRepository classEntityRepository;
    private final CourseRepository courseRepository;

    @Value("${assignment.max-title-length:100}")
    private int maxTitleLength;

    @Value("${assignment.min-due-date-days:1}")
    private int minDueDateDays;

    @Autowired
    public AssignmentService(AssignmentRepository assignmentRepository, AppUserRepository appUserRepository, ClassEntityRepository classEntityRepository, CourseRepository courseRepository) {
        this.assignmentRepository = assignmentRepository;
        this.appUserRepository = appUserRepository;
        this.classEntityRepository = classEntityRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Assignment updateAssignmentStatus(Long assignmentId, AssignmentStatus newStatus, AppUser currentUser) throws AccessDeniedException {
        Assignment assignment = findById(assignmentId);

        // Validate the status update based on user role and current status
        try {
            validateStatusUpdate(assignment, newStatus, currentUser);
        } catch (AccessDeniedException e) {
            throw new AccessDeniedException(e.getMessage());
        }

        assignment.setStatus(newStatus);
        return assignmentRepository.save(assignment);
    }

    private void validateStatusUpdate(Assignment assignment, AssignmentStatus newStatus, AppUser currentUser) throws AccessDeniedException {
        Role userRole = currentUser.getRole();

        // Admins and Coordinators can update to any status
        if (userRole == Role.ROLE_ADMIN || userRole == Role.ROLE_COORDINATOR) {
            return;
        }

        // Students can only update to SUBMITTED status
        if (userRole == Role.ROLE_STUDENT) {
            if (newStatus != AssignmentStatus.SUBMITTED) {
                throw new AccessDeniedException("Students can only update assignment status to SUBMITTED");
            }
            if (assignment.getStatus() != AssignmentStatus.PENDING) {
                throw new AccessDeniedException("Can only submit PENDING assignments");
            }
            return;
        }

        // Teachers can only update their own assignments to GRADED status
        if (userRole == Role.ROLE_TEACHER) {
            if (!assignment.getAssignedBy().equals(currentUser)) {
                throw new AccessDeniedException("Teachers can only update their own assignments");
            }
            if (newStatus != AssignmentStatus.GRADED) {
                throw new AccessDeniedException("Teachers can only update assignment status to GRADED");
            }
            if (assignment.getStatus() != AssignmentStatus.SUBMITTED) {
                throw new AccessDeniedException("Can only grade SUBMITTED assignments");
            }
        }

        if (assignment.getGrade() != null && newStatus != AssignmentStatus.GRADED) {
            throw new IllegalStateException("Cannot change status of graded assignments");
        }
    }

    @Transactional
    public List<AssignmentDTO> getAllAssignments() {
        List<Assignment> assignments = assignmentRepository.findAll();
        return assignments.stream()
                .map(assignment -> new AssignmentDTO(assignment, "Retrieved successfully"))
                .collect(Collectors.toList());
    }

    @Transactional
    public Assignment createAssignment(AssignmentRequestDTO dto, Long loggedInUserId)
            throws AccessDeniedException {
        try {
            log.info("Creating assignment with title: {}", dto.getTitle());

            validateAssignmentRequest(dto);

            AppUser teacher = appUserRepository.findById(loggedInUserId)
                    .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));

            ClassEntity classEntity = classEntityRepository.findClassEntityByName(dto.getClassName())
                    .orElseThrow(() -> new EntityNotFoundException("Class not found"));

            Course course = courseRepository.findCourseByName(dto.getCourseName())
                    .orElseThrow(() -> new EntityNotFoundException("Course not found"));

            validateTeacherAccess(teacher, classEntity);
            validateUniqueTitle(dto.getTitle(), classEntity); // No existingAssignmentId for creation

            Assignment assignment = createAssignmentEntity(dto, teacher, classEntity, course);

            log.info("Assignment created successfully with ID: {}", assignment.getId());
            return assignmentRepository.save(assignment);

        } catch (Exception e) {
            log.error("Error creating assignment: {}", e.getMessage());
            throw e;
        }
    }


    public List<AssignmentDTO> getAssignmentsByClass(Long studentId, AppUser loggedInUser)
            throws AccessDeniedException, EntityNotFoundException {
        AppUser user = appUserRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        ClassEntity classEntity = classEntityRepository.findById(classId).orElseThrow(
                () -> new EntityNotFoundException("Class not found")
        );

        // For teachers, only show their own assignments
        List<Assignment> assignments;
        if (user.getRole() == Role.ROLE_TEACHER) {
            assignments = assignmentRepository.findByClassEntityAndAssignedBy(classEntity, loggedInUser);
        } else {
            assignments = assignmentRepository.findByClassEntityOrderByDueDateDesc(classEntity);
        }

        return assignments.stream()
                .map(assignment -> new AssignmentDTO(assignment, ""))
                .collect(Collectors.toList());
    }

    @Transactional
    public Assignment updateAssignment(Long assignmentId, AssignmentRequestDTO dto, Long loggedInUserId)
            throws AccessDeniedException {
        try {
            log.info("Updating assignment ID: {} with title: {}", assignmentId, dto.getTitle());

            Assignment existingAssignment = assignmentRepository.findById(assignmentId)
                    .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));

            ClassEntity classEntity = classEntityRepository.findClassEntityByName(dto.getClassName())
                    .orElseThrow(() -> new EntityNotFoundException("Class not found"));

            Course course = courseRepository.findCourseByName(dto.getCourseName())
                    .orElseThrow(() -> new EntityNotFoundException("Course not found"));

            // Validate teacher access and title uniqueness
            validateTeacherAccess(existingAssignment.getAssignedBy(), classEntity);
            validateUniqueTitle(dto.getTitle(), classEntity, assignmentId);

            // Update assignment fields
            updateAssignmentFields(existingAssignment, dto, classEntity, course);

            log.info("Assignment updated successfully: {}", assignmentId);
            return assignmentRepository.save(existingAssignment);

        } catch (Exception e) {
            log.error("Error updating assignment {}: {}", assignmentId, e.getMessage());
            throw e;
        }
    }

    private void updateAssignmentFields(Assignment assignment, AssignmentRequestDTO dto,
                                        ClassEntity classEntity, Course course) {
        assignment.setTitle(dto.getTitle().trim());
        assignment.setDescription(dto.getDescription());
        assignment.setDueDate(dto.getDueDate());
        assignment.setClassEntity(classEntity);
        assignment.setCourse(course);
    }

    @Transactional
    public void deleteAssignment(Long assignmentId, Long loggedInUserId)
            throws AccessDeniedException {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));

        AppUser user = appUserRepository.findById(loggedInUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Only teacher who created the assignment or admin can delete it
        if (user.getRole() != Role.ROLE_ADMIN && !assignment.getAssignedBy().getId().equals(loggedInUserId)) {
            throw new AccessDeniedException("You can only delete your own assignments");
        }

        assignmentRepository.delete(assignment);
    }

    @Transactional
    public Assignment gradeAssignment(Long assignmentId, GradeDTO gradeDTO, AppUser currentUser)
            throws AccessDeniedException {
        Assignment assignment = findById(assignmentId);

        // Validate that only teachers can grade their own assignments
        if (currentUser.getRole() != Role.ROLE_TEACHER ||
                !assignment.getAssignedBy().equals(currentUser)) {
            throw new AccessDeniedException("Only the assigned teacher can grade this assignment");
        }

        // Validate that assignment is in SUBMITTED status
        if (assignment.getStatus() != AssignmentStatus.SUBMITTED) {
            throw new IllegalStateException("Can only grade assignments that have been submitted");
        }

        assignment.setGrade(gradeDTO.getGrade());
        assignment.setFeedback(gradeDTO.getFeedback());
        assignment.setStatus(AssignmentStatus.GRADED);

        return assignmentRepository.save(assignment);
    }

    @Transactional
    public Assignment unsubmitAssignment(Long assignmentId, AppUser currentUser)
            throws AccessDeniedException {
        Assignment assignment = findById(assignmentId);

        // Validate that only the student can unsubmit their assignment
        if (currentUser.getRole() != Role.ROLE_STUDENT) {
            throw new AccessDeniedException("Only students can unsubmit assignments");
        }

        // Check if the assignment belongs to the student's class
        if (!assignment.getClassEntity().getStudents().contains(currentUser)) {
            throw new AccessDeniedException("You can only unsubmit your own assignments");
        }

        // Validate that assignment is in SUBMITTED status and not yet graded
        if (assignment.getStatus() != AssignmentStatus.SUBMITTED) {
            throw new IllegalStateException("Can only unsubmit assignments in SUBMITTED status");
        }

        if (assignment.getGrade() != null) {
            throw new IllegalStateException("Cannot unsubmit graded assignments");
        }

        // Reset to pending status
        assignment.setStatus(AssignmentStatus.PENDING);

        return assignmentRepository.save(assignment);
    }

    public Assignment findById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));
    }

    @Cacheable(value = "assignments", key = "#courseId")
    public List<AssignmentDTO> getAssignmentsByCourse(Long courseId, AppUser loggedInUser)
            throws AccessDeniedException {
        AppUser user = appUserRepository.findById(loggedInUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Assignment> assignments;
        if (user.getRole() == Role.ROLE_TEACHER) {
            assignments = assignmentRepository.findByCourseIdAndAssignedBy(courseId, loggedInUser);
        } else {
            assignments = assignmentRepository.findByCourseIdOrderByDueDateDesc(courseId);
        }

        return assignments.stream()
                .map(assignment -> new AssignmentDTO(assignment, ""))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Assignment> createBatchAssignments(List<AssignmentRequestDTO> dtos, Long loggedInUserId)
            throws AccessDeniedException {
        return dtos.stream()
                .map(dto -> {
                    try {
                        return createAssignment(dto, loggedInUserId);
                    } catch (AccessDeniedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    private void validateAssignmentRequest(AssignmentRequestDTO dto) {
        if (dto.getTitle().length() > maxTitleLength) {
            throw new IllegalArgumentException("Title exceeds maximum length");
        }

        if (dto.getDueDate().isBefore(LocalDate.now().plusDays(minDueDateDays))) {
            throw new IllegalArgumentException("Due date must be at least " + minDueDateDays + " days in the future");
        }
    }

    private void validateTeacherAccess(AppUser teacher, ClassEntity classEntity)
            throws AccessDeniedException {
        if (teacher.getRole() == Role.ROLE_STUDENT) {
            throw new AccessDeniedException("Only teachers, admins, coordinators can create assignments");
        }

        if (teacher.getRole() == Role.ROLE_TEACHER &&
                !teacher.getTeacherDetails().getClasses().contains(classEntity.getId())) {
            throw new AccessDeniedException("Teachers can create assignments only for their assigned classes");
        }
    }

    private Assignment createAssignmentEntity(
            AssignmentRequestDTO dto,
            AppUser teacher,
            ClassEntity classEntity,
            Course course) {
        Assignment assignment = new Assignment();
        assignment.setTitle(dto.getTitle());
        assignment.setDescription(dto.getDescription());
        assignment.setDueDate(dto.getDueDate());
        assignment.setAssignedBy(teacher);
        assignment.setClassEntity(classEntity);
        assignment.setCourse(course);
        assignment.setDate(LocalDate.now());
        assignment.setStatus(AssignmentStatus.PENDING);

        return assignment;
    }

    private void validateUniqueTitle(String title, ClassEntity classEntity, Long... existingAssignmentId) {
        log.debug("Validating title uniqueness: {} for class: {}", title, classEntity.getName());

        // Normalize the title for comparison (trim whitespace and convert to lowercase)
        String normalizedTitle = title.trim().toLowerCase();

        // Check if any assignment with the same normalized title exists in the class
        Optional<Assignment> existingAssignment = assignmentRepository
                .findByTitleIgnoreCaseAndClassEntity(normalizedTitle, classEntity);

        if (existingAssignment.isPresent()) {
            // For update scenario, check if the found assignment is the same as the one being updated
            if (existingAssignmentId != null && existingAssignmentId.length > 0) {
                if (!existingAssignment.get().getId().equals(existingAssignmentId[0])) {
                    log.warn("Duplicate title found: {} for class: {}", title, classEntity.getName());
                    throw new IllegalArgumentException(
                            String.format("An assignment with title '%s' already exists in class %s",
                                    title, classEntity.getName())
                    );
                }
            } else {
                // For create scenario, any existing assignment with same title is a conflict
                log.warn("Attempted to create assignment with duplicate title: {} in class: {}",
                        title, classEntity.getName());
                throw new IllegalArgumentException(
                        String.format("An assignment with title '%s' already exists in class %s",
                                title, classEntity.getName())
                );
            }
        }
        log.debug("Title validation passed for: {} in class: {}", title, classEntity.getName());
    }
}