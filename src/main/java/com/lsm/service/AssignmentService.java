package com.lsm.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (assignmentRequestDTO.getTeacher().getRole() != Role.ROLE_TEACHER) {
            throw new AccessDeniedException("Only teachers can assign homework.");
        }

        List<AppUser> students = appUserRepository.findAllById(assignmentRequestDTO.getStudentIdList())
            .stream()
            .filter(user -> user.getRole() == Role.ROLE_STUDENT)
            .collect(Collectors.toList());

        Assignment assignment = new Assignment(assignmentRequestDTO.getTitle()
            , assignmentRequestDTO.getDescription(), assignmentRequestDTO.getDueDate(), assignmentRequestDTO.getTeacher(), students);
        return assignmentRepository.save(assignment);
    }

    public Assignment findById(Long id) {
        if (assignmentRepository.findById(id).isEmpty()) return null;
        return assignmentRepository.findById(id).get();
    }

    public Assignment save(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }
}
