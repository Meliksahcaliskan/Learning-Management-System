package com.lsm.service;

import com.lsm.model.entity.ClassEntity;
import com.lsm.model.entity.base.AppUser;
import com.lsm.repository.AppUserRepository;
import com.lsm.repository.ClassEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClassEntityService implements ClassEntityServiceInterface {

    private final ClassEntityRepository classRepository;
    private final AppUserRepository appUserRepository;
    private final ClassEntityRepository classEntityRepository;

    @Autowired
    public ClassEntityService(ClassEntityRepository classRepository, AppUserRepository appUserRepository, ClassEntityRepository classEntityRepository) {
        this.classRepository = classRepository;
        this.appUserRepository = appUserRepository;
        this.classEntityRepository = classEntityRepository;
    }

    @Override
    @Transactional
    public ClassEntity createClass(ClassEntity classEntity, Long teacherId, List<Long> studentIds) {
        // Find and set teacher
        AppUser teacher = appUserRepository.findById(teacherId)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + teacherId));
        classEntity.setTeacher(teacher);

        // Find and set students if provided
        if (studentIds != null && !studentIds.isEmpty()) {
            Set<AppUser > students = studentIds.stream()
                    .map(studentId -> appUserRepository.findById(studentId)
                            .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId)))
                    .peek(student -> student.getStudentDetails().setClassEntity(classEntity.getId()))
                    .collect(Collectors.toSet());

            classEntity.setStudents(students);
        }

        return classRepository.save(classEntity);
    }

    // @Override
    @Transactional
    public ClassEntity getClassById(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassEntity> getAllClasses(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User is not authenticated");
        }
        return classRepository.findAllWithAssociations();
    }

    @Override
    @Transactional
    public ClassEntity updateClass(Long id, ClassEntity classEntity, Long teacherId, List<Long> studentIds) {
        ClassEntity existingClass = getClassById(id);

        // Update basic fields
        existingClass.setName(classEntity.getName());
        existingClass.setDescription(classEntity.getDescription());

        // Update teacher if provided
        if (teacherId != null) {
            AppUser teacher = appUserRepository.findById(teacherId)
                    .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + teacherId));
            existingClass.setTeacher(teacher);
        }

        // Update students if provided
        if (studentIds != null) {
            Set<AppUser > students = studentIds.stream()
                    .map(studentId -> appUserRepository.findById(studentId)
                            .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId)))
                    .collect(Collectors.toSet());

            existingClass.setStudents(students);
        }

        return classRepository.save(existingClass);
    }

    @Override
    @Transactional
    public ClassEntity updateClass(Long id, ClassEntity classEntity) {
        ClassEntity existingClass = getClassById(id);

        existingClass.setName(classEntity.getName());
        existingClass.setDescription(classEntity.getDescription());

        // Only update teacher if provided in the entity
        if (classEntity.getTeacher() != null) {
            existingClass.setTeacher(classEntity.getTeacher());
        }

        // Only update students if provided in the entity
        if (classEntity.getStudents() != null) {
            existingClass.setStudents(classEntity.getStudents());
        }

        return classRepository.save(existingClass);
    }

    @Override
    @Transactional
    public void deleteClass(Long id) {
        if (!classRepository.existsById(id)) {
            throw new EntityNotFoundException("Class not found with id: " + id);
        }
        classRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ClassEntity addStudent(Long classId, Long studentId) {
        ClassEntity classEntity = getClassById(classId);
        AppUser student = appUserRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));

        if (classEntity.getStudents() == null) {
            classEntity.setStudents(new HashSet<>());
        }

        // Check if student is already in the class
        if (classEntity.getStudents().stream().noneMatch(s -> s.getId().equals(studentId))) {
            classEntity.getStudents().add(student);
            classRepository.save(classEntity);
        }
        return classEntity;
    }

    @Override
    @Transactional
    public ClassEntity removeStudent(Long classId, Long studentId) {
        ClassEntity classEntity = getClassById(classId);

        // Check if student exists before attempting to remove
        appUserRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));

        boolean removed = classEntity.getStudents().removeIf(student -> student.getId().equals(studentId));
        if (!removed) {
            throw new EntityNotFoundException("Student with id " + studentId + " not found in class " + classId);
        }

        classRepository.save(classEntity);
        return classEntity;
    }

    @Transactional(readOnly = true)
    public List<ClassEntity> getTeacherClasses(Authentication authentication) {
        AppUser teacher = (AppUser) authentication.getPrincipal();
        return classEntityRepository.findClassesByTeacherId(teacher.getId());
    }

    @Transactional
    public ClassEntity getStudentClasses(Authentication authentication) {
        AppUser student = (AppUser) authentication.getPrincipal();
        return classEntityRepository.getClassEntityById(student.getStudentDetails().getClassEntity()).orElseThrow
                (() -> new EntityNotFoundException("Class not found with id: " + student.getStudentDetails().getClassEntity()));
    }

}