package com.lsm.model.DTOs;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentFilterDTO {
    private Long classId;
    private Long courseId;
    private LocalDate dueDate;
}
