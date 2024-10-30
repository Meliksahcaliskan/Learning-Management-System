package com.lsm.controller;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lsm.model.DTOs.AssignmentDTO;
import com.lsm.model.DTOs.AssignmentRequestDTO;
import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.base.AppUser;
import com.lsm.service.AppUserService;
import com.lsm.service.AssignmentService;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    private AssignmentService assignmentService;
    private AppUserService appUserService;

    @Autowired
    public AssignmentController(AssignmentService assignmentService, AppUserService appUserService) {
        this.assignmentService = assignmentService;
        this.appUserService = appUserService;
    }

    @PostMapping("/createAssignment")
    public ResponseEntity<?> createAssignment(@RequestBody AssignmentRequestDTO assignmentRequestDTO, Principal principal) {
        try {
            // Extract the logged-in user's ID based on the principal (username)
            Long loggedInUserId = appUserService.findByUsername(principal.getName()).getId();

            // Call the service method with the logged-in user ID for authorization checks
            Assignment assignment = assignmentService.createAssignment(assignmentRequestDTO, loggedInUserId);

            AssignmentDTO assignmentDTO = new AssignmentDTO(assignment, "Assigned successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(assignmentDTO);
            
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new AssignmentDTO(null, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AssignmentDTO(null, e.getMessage()));
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

    @PutMapping("/updateAssignment/{assignmentId}")
    public ResponseEntity<?> updateAssignment(@PathVariable Long assignmentId, 
                                              @RequestBody AssignmentRequestDTO updateRequest,
                                              Authentication authentication) {
        AppUser userDetails = (AppUser) authentication.getPrincipal();
        Long loggedInTeacherId = userDetails.getId();

        try {
            Assignment updatedAssignment = assignmentService.updateAssignment(assignmentId, updateRequest, loggedInTeacherId);
            return ResponseEntity.ok(new AssignmentDTO(updatedAssignment, "Assignment updated successfully"));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteAssignment/{assignmentId}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Long assignmentId, Principal principal) {
        try {
            Long loggedInUserId = appUserService.findByUsername(principal.getName()).getId();
            assignmentService.deleteAssignment(assignmentId, loggedInUserId);
            return ResponseEntity.status(HttpStatus.OK).body("Assignment deleted successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
