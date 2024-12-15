package com.lsm.model.DTOs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkGradeRequestDTO {
    private List<BulkGradeItem> grades;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BulkGradeItem {
        @Positive
        private Long studentId;
        @Valid
        private GradeDTO grade;
    }
}
