package com.lsm.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.lsm.model.DTOs.AssignmentDTO;
import com.lsm.model.DTOs.AssignmentRequestDTO;
import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.base.AppUser;
import com.lsm.service.AppUserService;
import com.lsm.service.AssignmentService;

public class AssignmentControllerTest {

    @Mock
    private AssignmentService assignmentService;

    @Mock
    private AppUserService appUserService;

    @InjectMocks
    private AssignmentController assignmentController;

    @Mock
    private Principal principal;

    @Mock
    private Authentication authentication;

    private AppUser mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new AppUser();
        mockUser.setId(1L);
        mockUser.setUsername("teacherUser");
    }

    @Test
    void createAssignment_ShouldReturnCreatedStatus_WhenSuccessful() throws Exception {
        when(principal.getName()).thenReturn("teacherUser");
        when(appUserService.findByUsername("teacherUser")).thenReturn(mockUser);
        when(assignmentService.createAssignment(any(AssignmentRequestDTO.class), any(Long.class)))
                .thenReturn(new Assignment());

        AssignmentRequestDTO requestDTO = new AssignmentRequestDTO();
        ResponseEntity<?> response = assignmentController.createAssignment(requestDTO, principal);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createAssignment_ShouldReturnForbidden_WhenAccessDenied() throws Exception {
        when(principal.getName()).thenReturn("teacherUser");
        when(appUserService.findByUsername("teacherUser")).thenReturn(mockUser);
        when(assignmentService.createAssignment(any(AssignmentRequestDTO.class), any(Long.class)))
                .thenThrow(new AccessDeniedException("Access denied"));

        AssignmentRequestDTO requestDTO = new AssignmentRequestDTO();
        ResponseEntity<?> response = assignmentController.createAssignment(requestDTO, principal);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void displayAssignmentsForStudent_ShouldReturnOkStatus_WhenSuccessful() throws Exception {
        Assignment assignment = new Assignment(); // TODO: fill with placeholders
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(assignmentService.displayAssignmentsForStudent(any(Long.class), any(Long.class)))
                .thenReturn(Collections.singletonList(new AssignmentDTO(assignment, "Assigned succesfully.")));

        ResponseEntity<?> response = assignmentController.displayAssignmentsForStudent(1L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateAssignment_ShouldReturnOkStatus_WhenSuccessful() throws Exception {
        when(authentication.getPrincipal()).thenReturn(mockUser);
        when(assignmentService.updateAssignment(any(Long.class), any(AssignmentRequestDTO.class), any(Long.class)))
                .thenReturn(new Assignment());

        AssignmentRequestDTO updateRequest = new AssignmentRequestDTO();
        ResponseEntity<?> response = assignmentController.updateAssignment(1L, updateRequest, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteAssignment_ShouldReturnOkStatus_WhenSuccessful() throws Exception {
        when(principal.getName()).thenReturn("teacherUser");
        when(appUserService.findByUsername("teacherUser")).thenReturn(mockUser);

        ResponseEntity<?> response = assignmentController.deleteAssignment(1L, principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
