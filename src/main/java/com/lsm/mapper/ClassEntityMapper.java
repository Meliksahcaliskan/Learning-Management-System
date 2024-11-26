package com.lsm.mapper;

import com.lsm.model.DTOs.ClassEntityRequestDTO;
import com.lsm.model.DTOs.ClassEntityResponseDTO;
import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.ClassEntity;
import com.lsm.model.entity.base.AppUser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
        Long teacherId = entity.getTeacher().getId();
        dto.setTeacherId(teacherId);

        // Map students
        if (entity.getStudents() != null) {
            List<Long> studentIds = entity.getStudents().stream()
                    .map(AppUser::getId)
                    .collect(Collectors.toList());
            dto.setStudentIds(studentIds);
        } else {
            ArrayList<Long> studentIds = new ArrayList<>();
            dto.setStudentIds(studentIds);
        }

        // Map assignments
        if (entity.getAssignments() != null) {
            List<Long> assignmentIds = entity.getAssignments().stream()
                    .map(Assignment::getId)
                    .collect(Collectors.toList());
            dto.setAssignmentIds(assignmentIds);
        } else {
            ArrayList<Long> assignmentIds = new ArrayList<>();
            dto.setAssignmentIds(assignmentIds);
        }

        return dto;
    }
}