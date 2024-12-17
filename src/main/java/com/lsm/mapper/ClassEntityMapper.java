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
        dto.setClassId(entity.getId());

        // Map teacher
        if (entity.getTeacher() != null) {
            dto.setTeacherId(entity.getTeacher().getId());
        }

        // Map students with their IDs and names
        if (entity.getStudents() != null && !entity.getStudents().isEmpty()) {
            Map<Long, String> studentMappings = entity.getStudents().stream()
                    .collect(Collectors.toMap(
                            AppUser::getId,
                            student -> student.getName() + " " + student.getSurname()
                    ));
            dto.setStudentIdAndNames(studentMappings);
        } else {
            dto.setStudentIdAndNames(new HashMap<>());
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