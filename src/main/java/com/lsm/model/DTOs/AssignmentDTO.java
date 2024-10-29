package com.lsm.model.DTOs;

import java.time.LocalDate;

import com.lsm.model.entity.Assignment;

public class AssignmentDTO {
    private Long id; // Unique identifier for the assignment
    private String title;
    private String description;
    private LocalDate dueDate;

    // Default constructor
    // public AssignmentDTO() {}

    // Parameterized constructor
    public AssignmentDTO(Assignment assignment) {
        this.id = assignment.getId();
        this.title = assignment.getTitle();
        this.description = assignment.getDescription();
        this.dueDate = assignment.getDueDate();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
