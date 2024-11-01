package com.lsm.service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.lsm.model.DTOs.AssignmentDTO;
import com.lsm.model.DTOs.AssignmentRequestDTO;
import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import com.lsm.repository.AppUserRepository;
import com.lsm.repository.AssignmentRepository;

class AssignmentServiceTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private AssignmentService assignmentService;

    private AppUser teacher;
    private AppUser student;
    private Assignment assignment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        teacher = new AppUser();
        teacher.setId(1L);
        teacher.setRole(Role.ROLE_TEACHER);
        teacher.setClasses(new ArrayList<>()); // Initialize classes list

        student = new AppUser();
        student.setId(2L);
        student.setRole(Role.ROLE_STUDENT);
        student.setClasses(new ArrayList<>()); // Initialize classes list

        assignment = new Assignment();
        assignment.setId(1L);
        assignment.setTitle("Math Assignment"); // Set the title here
        assignment.setAssignedBy(teacher);
        assignment.setAssignedTo(List.of(student));
    }

    @Test
    void testCreateAssignment_Success() throws AccessDeniedException {
        AssignmentRequestDTO requestDTO = new AssignmentRequestDTO();
        requestDTO.setTitle("Math Assignment");
        requestDTO.setDescription("Chapter 1 Exercises");
        requestDTO.setDueDate(LocalDate.now().plusDays(7));
        requestDTO.setStudentIdList(List.of(student.getId()));
        requestDTO.setClassId(1L);

        List<Long> classes = new ArrayList<>(); classes.add(1L);
        teacher.setClasses(classes);

        when(appUserRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(appUserRepository.findAllById(requestDTO.getStudentIdList())).thenReturn(List.of(student));
        when(assignmentRepository.save(any(Assignment.class))).thenReturn(assignment);

        Assignment createdAssignment = assignmentService.createAssignment(requestDTO, teacher.getId());

        assertNotNull(createdAssignment);
        assertEquals("Math Assignment", createdAssignment.getTitle());
    }

    @Test
    void testCreateAssignment_AccessDenied() {
        // Prepare the assignment request
        AssignmentRequestDTO requestDTO = new AssignmentRequestDTO();
        requestDTO.setTitle("Math Assignment");
        requestDTO.setDescription("Chapter 1 Exercises");
        requestDTO.setDueDate(LocalDate.now().plusDays(7));
        requestDTO.setStudentIdList(List.of(student.getId()));
        
        // Set a class ID that is not in the teacher's allowed classes
        requestDTO.setClassId(1L);
        
        // Ensure the teacher's class list does NOT include the class in the request
        List<Long> classes = new ArrayList<>();
        classes.add(2L); // Different from the requestDTO's class ID
        teacher.setClasses(classes);

        // Mock the repository calls
        when(appUserRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(appUserRepository.findAllById(requestDTO.getStudentIdList())).thenReturn(List.of(student));

        // Use assertThrows to verify the AccessDeniedException is thrown
        assertThrows(AccessDeniedException.class, () -> {
            assignmentService.createAssignment(requestDTO, teacher.getId());
        });
    }

    @Test
    void testDisplayAssignmentsForStudent_Success() throws AccessDeniedException {
        // Ensure the assignment has a title
        assignment.setTitle("Math Assignment");

        when(appUserRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(assignmentRepository.findByAssignedToContaining(student)).thenReturn(List.of(assignment));

        List<AssignmentDTO> assignments = assignmentService.displayAssignmentsForStudent(student.getId(), student.getId());

        assertNotNull(assignments);
        assertEquals(1, assignments.size());
        assertEquals("Math Assignment", assignments.get(0).getTitle());
    }

    @Test
    void testDeleteAssignment_UnauthorizedForStudent() {
        when(appUserRepository.findById(student.getId())).thenReturn(Optional.of(student));
        when(assignmentRepository.findById(assignment.getId())).thenReturn(Optional.of(assignment));

        assertThrows(AccessDeniedException.class, () -> {
            assignmentService.deleteAssignment(assignment.getId(), student.getId());
        });
    }

    @Test
    void testUpdateAssignment_UnauthorizedTeacher() {
        AssignmentRequestDTO updateRequest = new AssignmentRequestDTO();
        updateRequest.setTitle("Updated Assignment Title");

        when(assignmentRepository.findById(assignment.getId())).thenReturn(Optional.of(assignment));
        when(appUserRepository.findById(student.getId())).thenReturn(Optional.of(student));

        assertThrows(AccessDeniedException.class, () -> {
            assignmentService.updateAssignment(assignment.getId(), updateRequest, student.getId());
        });
    }
}
