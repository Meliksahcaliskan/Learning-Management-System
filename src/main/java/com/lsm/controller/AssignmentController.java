package com.lsm.controller;

import java.nio.file.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lsm.model.DTOs.AssignmentDTO;
import com.lsm.model.DTOs.AssignmentRequestDTO;
import com.lsm.model.entity.Assignment;
import com.lsm.service.AssignmentService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/assignment")
public class AssignmentController {
    AssignmentService assignmentService;

    @Autowired
    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assign(@RequestBody AssignmentRequestDTO assignmentRequestDTO) {
        try {
            // Authenticate user
            Assignment assignment = assignmentService.createAssignment(assignmentRequestDTO);
            
            // Create login response DTO
            AssignmentDTO assignmentDTO = new AssignmentDTO(assignment);
            
            // Return the successful login response
            return ResponseEntity.ok(assignmentDTO);
            
        } catch (AccessDeniedException e) {
            // Return 401 Unauthorized when credentials are invalid
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}
