package com.lsm.model.DTOs;

import java.time.LocalDate;

import com.lsm.model.entity.AssignmentDocument;
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

    @NotNull(message = "Class name is required")
    @Size(min = 2, max = 50, message = "Class name must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9-_ ]+$", message = "Class name can only contain letters, numbers, spaces, hyphens and underscores")
    private String className;

    @NotNull(message = "Course name is required")
    @Size(min = 2, max = 100, message = "Course name must be between 2 and 100 characters")
    @Pattern(regexp = "^[A-Za-z0-9-_ ]+$", message = "Course name can only contain letters, numbers, spaces, hyphens and underscores")
    private String courseName;

    private AssignmentDocumentDTO document;
}
