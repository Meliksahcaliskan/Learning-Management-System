package com.lsm.model.DTOs;

import com.lsm.model.entity.enums.AssignmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentStatusUpdateDTO {
    @NotNull(message = "Status cannot be null")
    private AssignmentStatus status;
}