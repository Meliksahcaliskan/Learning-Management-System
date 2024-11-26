package com.lsm.model.DTOs;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentRequestDTO {

    @NotNull(message = "Teacher ID is required.")
    private Long teacherId;

    @NotNull(message = "Title is required.")
    @Size(min = 3, max = 100, message = "Title should be between 3 and 100 characters.")
    private String title;

    @Size(max = 1000, message = "Description should not exceed 1000 characters.")
    private String description;

    @Future(message = "Due date should be in the future.")
    private LocalDate dueDate;

    @NotNull(message = "Class name is required.")
    private String className;

    @NotNull(message = "Course name is required.")
    private String courseName;

    @NotNull(message = "Assignment date is required.")
    private LocalDate date;

    // Default constructor
    public AssignmentRequestDTO() {
    }

    // Parameterized constructor
    public AssignmentRequestDTO(Long teacherId, String title, String description, LocalDate dueDate, 
                                String className, String courseName) {
        this.teacherId = teacherId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.className = className;
        this.courseName = courseName;
        this.date = LocalDate.now();
    }
}
