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
import jakarta.persistence.EntityNotFoundException;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AppUserRepository appUserRepository;

    @Autowired
    public AssignmentService(AssignmentRepository assignmentRepository, AppUserRepository appUserRepository) {
        this.assignmentRepository = assignmentRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public Assignment createAssignment(AssignmentRequestDTO dto, Long loggedInUserId)
            throws AccessDeniedException {
        // Validate teacher
        AppUser teacher = appUserRepository.findById(loggedInUserId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));

        if (teacher.getRole() != Role.ROLE_TEACHER && teacher.getRole() != Role.ROLE_ADMIN) {
            throw new AccessDeniedException("Only teachers and admins can create assignments");
        }

        // Validate that teacher ID matches logged-in user
        if (!dto.getTeacherId().equals(loggedInUserId)) {
            throw new AccessDeniedException("Teacher ID must match logged in user");
        }

        // Check if assignment title already exists for the class
        if (assignmentRepository.existsByTitleAndClassId(dto.getTitle(), dto.getClassId())) {
            throw new IllegalArgumentException("An assignment with this title already exists for this class");
        }

        Assignment assignment = new Assignment();
        assignment.setTitle(dto.getTitle());
        assignment.setDescription(dto.getDescription());
        assignment.setDueDate(dto.getDueDate());
        assignment.setAssignedBy(teacher);
        assignment.setClassId(dto.getClassId());
        assignment.setCourseId(dto.getCourseId());
        assignment.setDate(LocalDate.now());

        return assignmentRepository.save(assignment);
    }

    public List<AssignmentDTO> getAssignmentsByClass(Long classId, AppUser loggedInUser)
            throws AccessDeniedException {
        AppUser user = appUserRepository.findById(loggedInUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // For teachers, only show their own assignments
        List<Assignment> assignments;
        if (user.getRole() == Role.ROLE_TEACHER) {
            assignments = assignmentRepository.findByClassIdAndAssignedBy(classId, loggedInUser);
        } else {
            assignments = assignmentRepository.findByClassIdOrderByDueDateDesc(classId);
        }

        return assignments.stream()
                .map(assignment -> new AssignmentDTO(assignment, ""))
                .collect(Collectors.toList());
    }

    @Transactional
    public Assignment updateAssignment(Long assignmentId, AssignmentRequestDTO dto, Long loggedInUserId)
            throws AccessDeniedException {
        Assignment existingAssignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));

        // Validate teacher
        if (!existingAssignment.getAssignedBy().getId().equals(loggedInUserId)) {
            throw new AccessDeniedException("You can only update your own assignments");
        }

        // Check if updated title conflicts with existing assignments
        if (!existingAssignment.getTitle().equals(dto.getTitle()) &&
                assignmentRepository.existsByTitleAndClassId(dto.getTitle(), dto.getClassId())) {
            throw new IllegalArgumentException("An assignment with this title already exists for this class");
        }

        // Update fields
        existingAssignment.setTitle(dto.getTitle());
        existingAssignment.setDescription(dto.getDescription());
        existingAssignment.setDueDate(dto.getDueDate());
        existingAssignment.setClassId(dto.getClassId());
        existingAssignment.setCourseId(dto.getCourseId());

        return assignmentRepository.save(existingAssignment);
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

    public Assignment findById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));
    }

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
}