package com.lsm.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lsm.model.DTOs.AssignmentDTO;
import com.lsm.model.DTOs.AssignmentRequestDTO;
import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.base.AppUser;
import com.lsm.service.AssignmentService;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    private AssignmentService assignmentService;

    @Autowired
    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/createAssignment")
    public ResponseEntity<?> createAssignment(@RequestBody AssignmentRequestDTO assignmentRequestDTO) {
        try {
            Assignment assignment = assignmentService.createAssignment(assignmentRequestDTO);
            AssignmentDTO assignmentDTO = new AssignmentDTO(assignment, "Assigned succesfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(assignmentDTO);
            
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AssignmentDTO(null, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/displayAssignments/{studentId}")
    public ResponseEntity<?> displayAssignmentsForStudent(@PathVariable Long studentId, Authentication authentication) {
        AppUser userDetails = (AppUser) authentication.getPrincipal();
        Long loggedInStudentId = userDetails.getId();

        try {
            List<AssignmentDTO> assignments = assignmentService.displayAssignmentsForStudent(studentId, loggedInStudentId);
            return ResponseEntity.ok(assignments);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
