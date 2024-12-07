package com.lsm.model.DTOs;

import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentRequestDTO {
    @NotNull(message = "Teacher ID is required")
    @Positive(message = "Teacher ID must be positive")
    private Long teacherId;

    @NotNull(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    private LocalDate dueDate;

    @NotNull(message = "Class id is required")
    private Long classId;

    @NotNull(message = "Course id is required")
    private Long courseId;

    private AssignmentDocumentDTO document;
}
