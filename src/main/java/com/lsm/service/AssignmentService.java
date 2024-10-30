package com.lsm.service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lsm.model.DTOs.AssignmentDTO;
import com.lsm.model.DTOs.AssignmentRequestDTO;
import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import com.lsm.repository.AppUserRepository;
import com.lsm.repository.AssignmentRepository;

import jakarta.transaction.Transactional;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    public Assignment createAssignment(AssignmentRequestDTO assignmentRequestDTO, Long loggedInUserId) 
            throws AccessDeniedException, IllegalArgumentException {
        AppUser user = appUserRepository.findById(loggedInUserId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if the user has permission to add assignments
        if (user.getRole() == Role.ROLE_STUDENT) {
            throw new AccessDeniedException("Students cannot add assignments.");
        }

        if (user.getRole() == Role.ROLE_TEACHER && !user.getClasses().contains(assignmentRequestDTO.getClassId())) {
            throw new AccessDeniedException("Teachers can only add assignments for their own classes.");
        }

        // Get list of students to assign
        List<AppUser> students = appUserRepository.findAllById(assignmentRequestDTO.getStudentIdList())
            .stream()
            .filter(appUser -> appUser.getRole() == Role.ROLE_STUDENT)
            .collect(Collectors.toList());

        Assignment assignment = new Assignment(
            assignmentRequestDTO.getTitle(),
            assignmentRequestDTO.getDescription(),
            assignmentRequestDTO.getDueDate(),
            user,
            students,
            assignmentRequestDTO.getClassId(),
            assignmentRequestDTO.getCourseId(),
            LocalDate.now()
        );

        return assignmentRepository.save(assignment);
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
}
