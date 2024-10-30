package com.lsm.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
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

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    public Assignment createAssignment(AssignmentRequestDTO assignmentRequestDTO) 
        throws AccessDeniedException {
        Long teacherId = assignmentRequestDTO.getTeacherId();
        if (teacherId == null) {
            throw new IllegalArgumentException("Teacher ID cannot be null.");
        }
        Optional<AppUser> teacher_opt = appUserRepository.findById(teacherId);
        if(!teacher_opt.isPresent()) {
            throw new IllegalArgumentException("The teacher id doesn't exist or doesn't belongs to a teacher.");
        }
        AppUser teacher = teacher_opt.get();
        if (teacher.getRole() != Role.ROLE_TEACHER) {
            throw new AccessDeniedException("Only teachers can assign homework.");
        }

        List<AppUser> students = appUserRepository.findAllById(assignmentRequestDTO.getStudentIdList())
            .stream()
            .filter(user -> user.getRole() == Role.ROLE_STUDENT)
            .collect(Collectors.toList());

        Assignment assignment = new Assignment(assignmentRequestDTO.getTitle()
            , assignmentRequestDTO.getDescription(), assignmentRequestDTO.getDueDate(), teacher, students);
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

    public Assignment findById(Long id) {
        if (assignmentRepository.findById(id).isEmpty()) return null;
        return assignmentRepository.findById(id).get();
    }

    public Assignment save(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }
}
