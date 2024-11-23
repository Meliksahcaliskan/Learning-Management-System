package com.lsm.service;

import com.lsm.model.entity.ClassEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ClassEntityServiceInterface {
    ClassEntity createClass(ClassEntity classEntity, Long teacherId, List<Long> studentIds);
    ClassEntity updateClass(Long id, ClassEntity classEntity, Long teacherId, List<Long> studentIds);
    List<ClassEntity> getAllClasses(Authentication authentication);
    ClassEntity updateClass(Long id, ClassEntity classEntity);
    void deleteClass(Long id);
    ClassEntity addStudent(Long classId, Long studentId);
    ClassEntity removeStudent(Long classId, Long studentId);
}
