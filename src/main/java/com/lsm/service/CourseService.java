package com.lsm.service;

import com.lsm.model.DTOs.CourseDTO;
import com.lsm.model.entity.Course;
import com.lsm.repository.ClassEntityRepository;
import com.lsm.repository.CourseRepository;
import com.lsm.exception.DuplicateResourceException;
import com.lsm.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final ClassEntityRepository classEntityRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, ClassEntityRepository classEntityRepository) {
        this.courseRepository = courseRepository;
        this.classEntityRepository = classEntityRepository;
    }

    @Cacheable(value = "courses", key = "#id")
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return mapToDTO(course);
    }

    @Cacheable(value = "courses", key = "'all'")
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CourseDTO> getCoursesByClassId(Long classId) {
        return courseRepository.findByClassId(classId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = "courses", allEntries = true)
    public CourseDTO createCourse(CourseDTO courseDTO) {
        if (courseRepository.existsByCode(courseDTO.getCode())) {
            throw new DuplicateResourceException("Course with code " + courseDTO.getCode() + " already exists");
        }

        Course course = Course.builder()
                .name(courseDTO.getName())
                .description(courseDTO.getDescription())
                .code(courseDTO.getCode())
                .credits(courseDTO.getCredits())
                .classes(courseDTO.getClassEntityIds().stream()
                        .flatMap(id -> classEntityRepository.getClassEntityById(id).stream())
                        .toList())
                .build();

        Course savedCourse = courseRepository.save(course);
        return mapToDTO(savedCourse);
    }

    @CacheEvict(value = "courses", key = "#id")
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        // Check if the new code is different and already exists
        if (!course.getCode().equals(courseDTO.getCode()) &&
                courseRepository.existsByCode(courseDTO.getCode())) {
            throw new DuplicateResourceException("Course with code " + courseDTO.getCode() + " already exists");
        }

        course.setName(courseDTO.getName());
        course.setDescription(courseDTO.getDescription());
        course.setCode(courseDTO.getCode());
        course.setCredits(courseDTO.getCredits());

        Course updatedCourse = courseRepository.save(course);
        return mapToDTO(updatedCourse);
    }

    @CacheEvict(value = "courses", allEntries = true)
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    private CourseDTO mapToDTO(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .code(course.getCode())
                .credits(course.getCredits())
                .build();
    }
}