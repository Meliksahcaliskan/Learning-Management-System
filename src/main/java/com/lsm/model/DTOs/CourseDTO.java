package com.lsm.model.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "Course name is required")
    private String name;

    private String description;

    @NotBlank(message = "Course code is required")
    private String code;

    @Min(value = 1, message = "Credits must be at least 1")
    private Integer credits;
}