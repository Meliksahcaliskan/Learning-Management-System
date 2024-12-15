package com.lsm.mapper;

import com.lsm.model.DTOs.ClassEntityRequestDTO;
import com.lsm.model.DTOs.ClassEntityResponseDTO;
import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.ClassEntity;
import com.lsm.model.entity.base.AppUser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ClassEntityMapper {

    public ClassEntity toEntity(ClassEntityRequestDTO dto) {
        ClassEntity entity = new ClassEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    public ClassEntityResponseDTO toDTO(ClassEntity entity) {
        ClassEntityResponseDTO dto = new ClassEntityResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());

        // Map teacher
        if (entity.getTeacher() != null) {
            dto.setTeacherId(entity.getTeacher().getId());
        }

        // Map students with their IDs and names
        if (entity.getStudents() != null && !entity.getStudents().isEmpty()) {
            List<Map<Long, String>> studentMappings = entity.getStudents().stream()
                    .map(student -> {
                        Map<Long, String> mapping = new HashMap<>();
                        mapping.put(student.getId(), student.getName() + " " + student.getSurname());  // Assuming AppUser has getName()
                        return mapping;
                    })
                    .collect(Collectors.toList());
            dto.setStudentIdAndNames(studentMappings);
        } else {
            dto.setStudentIdAndNames(new ArrayList<>());
        }

        // Map assignments
        if (entity.getAssignments() != null && !entity.getAssignments().isEmpty()) {
            List<Long> assignmentIds = entity.getAssignments().stream()
                    .map(Assignment::getId)
                    .collect(Collectors.toList());
            dto.setAssignmentIds(assignmentIds);
        } else {
            dto.setAssignmentIds(new ArrayList<>());
        }

        return dto;
    }
}