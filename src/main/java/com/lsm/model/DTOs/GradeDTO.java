package com.lsm.model.DTOs;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GradeDTO {
    @NotNull(message = "Grade is required")
    @DecimalMin(value = "0.0", message = "Grade cannot be less than 0")
    @DecimalMax(value = "100.0", message = "Grade cannot be more than 100")
    private Double grade;

    private String feedback;
}
