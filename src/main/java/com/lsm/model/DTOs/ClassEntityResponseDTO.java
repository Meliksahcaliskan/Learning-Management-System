package com.lsm.model.DTOs;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class ClassEntityResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Long classId;
    private Long teacherId;
    private Map<Long, String> studentIdAndNames;
    private List<Long> assignmentIds;
}

