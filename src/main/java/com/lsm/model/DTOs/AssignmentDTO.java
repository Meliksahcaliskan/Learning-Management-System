package com.lsm.model.DTOs;

import java.time.LocalDate;

import com.lsm.model.entity.Assignment;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentDTO {
    private Long id; // Unique identifier for the assignment
    private String title;
    private String description;
    private LocalDate dueDate;
    private String message;

    // Default constructor
    // public AssignmentDTO() {}

    // Parameterized constructor
    public AssignmentDTO(Assignment assignment, String message) {
        this.id = assignment.getId();
        this.title = assignment.getTitle();
        this.description = assignment.getDescription();
        this.dueDate = assignment.getDueDate();
        this.message = message;
    }
}
