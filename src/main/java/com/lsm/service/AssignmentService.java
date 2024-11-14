package com.lsm.service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.lsm.model.DTOs.AssignmentDTO;
import com.lsm.model.DTOs.AssignmentRequestDTO;
import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import com.lsm.repository.AppUserRepository;
import com.lsm.repository.AssignmentRepository;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @Transactional
    public Assignment createAssignment(AssignmentRequestDTO assignmentRequestDTO, Long loggedInUserId)
            throws AccessDeniedException, IllegalArgumentException {
        
        // Validate input
        validateInputDTO(assignmentRequestDTO);
        
        // Find and validate user
        AppUser user = findAndValidateUser(loggedInUserId);
        
        // Validate user permissions
        validateUserPermissions(user, assignmentRequestDTO);
        
        // Validate and retrieve students
        List<AppUser> students = findValidStudents(assignmentRequestDTO);
        
        try {
            // Create assignment
            Assignment assignment = buildAssignment(assignmentRequestDTO, user, students);
            
            // Save assignment
            return assignmentRepository.save(assignment);
        } catch (Exception e) {
            // You can still throw a meaningful exception
            throw new RuntimeException("Failed to create assignment", e);
        }
    }

    public List<AssignmentDTO> displayAssignmentsForStudent(Long requestedStudentId, Long loggedInStudentId)
        throws IllegalArgumentException, AccessDeniedException {
        AppUser student = appUserRepository.findById(loggedInStudentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        
        if (!requestedStudentId.equals(loggedInStudentId)) {
            throw new AccessDeniedException("You are not authorized to view other students' assignments.");
        }
        
        if (student.getRole() != Role.ROLE_STUDENT) {
            throw new AccessDeniedException("Only students can view assigned homework.");
        }

        List<Assignment> assignments = assignmentRepository.findByAssignedToContaining(student);
        return assignments.stream()
            .map(assignment -> new AssignmentDTO(assignment, ""))
            .collect(Collectors.toList());
    }

    public Assignment updateAssignment(Long assignmentId, AssignmentRequestDTO updateRequest, Long loggedInTeacherId)
            throws AccessDeniedException {
        
        Assignment assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));
        
        if (!assignment.getAssignedBy().getId().equals(loggedInTeacherId)) {
            throw new AccessDeniedException("You are not authorized to update this assignment.");
        }

        // Only teachers are allowed to update assignments
        AppUser teacher = appUserRepository.findById(loggedInTeacherId)
            .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        
        if (teacher.getRole() != Role.ROLE_TEACHER) {
            throw new AccessDeniedException("Only teachers can update assignments.");
        }

        // Update the assignment's properties based on the updateRequest
        if (updateRequest.getTitle() != null) {
            assignment.setTitle(updateRequest.getTitle());
        }
        
        if (updateRequest.getDescription() != null) {
            assignment.setDescription(updateRequest.getDescription());
        }
        
        if (updateRequest.getDueDate() != null) {
            assignment.setDueDate(updateRequest.getDueDate());
        }

        if (updateRequest.getClassId() != null) {
            assignment.setClassId(updateRequest.getClassId());
        }

        if (updateRequest.getCourseId() != null) {
            assignment.setCourseId(updateRequest.getCourseId());
        }

        if (updateRequest.getStudentIdList() != null && !updateRequest.getStudentIdList().isEmpty()) {
            List<AppUser> students = appUserRepository.findAllById(updateRequest.getStudentIdList()).stream()
                .filter(user -> user.getRole() == Role.ROLE_STUDENT)
                .collect(Collectors.toList());
            assignment.setAssignedTo(students);
        }

        return assignmentRepository.save(assignment);
    }

    @Transactional
    public void deleteAssignment(Long assignmentId, Long loggedInUserId) throws AccessDeniedException {
        Assignment assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        AppUser user = appUserRepository.findById(loggedInUserId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Role userRole = user.getRole();

        // Check user permissions
        if (userRole == Role.ROLE_STUDENT) {
            throw new AccessDeniedException("Students cannot delete assignments.");
        }

        if (userRole == Role.ROLE_TEACHER) {
            // Teachers can only delete assignments they assigned
            if (!assignment.getAssignedBy().getId().equals(loggedInUserId)) {
                throw new AccessDeniedException("Teachers can only delete assignments they have assigned.");
            }
        }

        // Coordinators and Admins can delete any assignment
        if (userRole == Role.ROLE_COORDINATOR || userRole == Role.ROLE_ADMIN) {
            assignmentRepository.delete(assignment);
        } else {
            throw new AccessDeniedException("You are not authorized to delete this assignment.");
        }
    }

    public Assignment findById(Long id) {
        if (assignmentRepository.findById(id).isEmpty()) return null;
        return assignmentRepository.findById(id).get();
    }

    public Assignment save(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    private void validateInputDTO(AssignmentRequestDTO dto) {
        Objects.requireNonNull(dto, "Assignment request cannot be null");
        
        if (StringUtils.isBlank(dto.getTitle())) {
            throw new IllegalArgumentException("Assignment title is required");
        }
        
        if (dto.getDueDate() == null) {
            throw new IllegalArgumentException("Due date is required");
        }
        
        if (dto.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
        
        if (CollectionUtils.isEmpty(dto.getStudentIdList())) {
            throw new IllegalArgumentException("At least one student must be assigned");
        }
    }
    
    private AppUser findAndValidateUser(Long userId) {
        return appUserRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
    
    private void validateUserPermissions(AppUser user, AssignmentRequestDTO dto) throws AccessDeniedException {
        // Check user role
        if (user.getRole() == Role.ROLE_STUDENT) {
            throw new AccessDeniedException("Students cannot add assignments");
        }
        
        // For teachers, check class access
        if (user.getRole() == Role.ROLE_TEACHER) {
            if (dto.getClassId() == null) {
                throw new IllegalArgumentException("Class ID is required for teacher assignments");
            }
            
            if (!user.getClasses().contains(dto.getClassId())) {
                throw new AccessDeniedException("Teachers can only add assignments for their own classes");
            }
        }
    }
    
    private List<AppUser> findValidStudents(AssignmentRequestDTO dto) {
        List<AppUser> students = appUserRepository.findAllById(dto.getStudentIdList())
            .stream()
            .filter(appUser -> appUser.getRole() == Role.ROLE_STUDENT)
            .collect(Collectors.toList());
        
        if (students.size() != dto.getStudentIdList().size()) {
            throw new IllegalArgumentException("Invalid student IDs provided");
        }
        
        return students;
    }
    
    private Assignment buildAssignment(AssignmentRequestDTO dto, AppUser creator, List<AppUser> students) {
        return new Assignment(
            dto.getTitle(),
            dto.getDescription(),
            dto.getDueDate(),
            creator,
            students,
            dto.getClassId(),
            dto.getCourseId(),
            LocalDate.now()
        );
    }
}
