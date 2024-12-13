package com.lsm.service;

import com.lsm.exception.DuplicateResourceException;
import com.lsm.exception.ResourceNotFoundException;
import com.lsm.model.DTOs.CourseDTO;
import com.lsm.model.entity.Course;
import com.lsm.model.entity.ClassEntity;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import com.lsm.repository.AppUserRepository;
import com.lsm.repository.ClassEntityRepository;
import com.lsm.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ClassEntityRepository classEntityRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private CourseService courseService;

    private AppUser testTeacher;
    private AppUser testStudent;
    private Course testCourse;
    private ClassEntity testClass;
    private CourseDTO testCourseDTO;

    @BeforeEach
    void setUp() {
        testTeacher = AppUser.builder()
                .id(1L)
                .role(Role.ROLE_TEACHER)
                .build();

        testStudent = AppUser.builder()
                .id(2L)
                .role(Role.ROLE_STUDENT)
                .build();

        testClass = ClassEntity.builder()
                .id(1L)
                .name("Test Class")
                .teacher(testTeacher)
                .build();

        testCourse = Course.builder()
                .id(1L)
                .name("Test Course")
                .code("TEST101")
                .credits(3)
                .description("Test Description")
                .build();

        testCourseDTO = CourseDTO.builder()
                .id(1L)
                .name("Test Course")
                .code("TEST101")
                .credits(3)
                .description("Test Description")
                .build();
    }

    @Nested
    @DisplayName("Get Course Tests")
    class GetCourseTests {

        @Test
        @DisplayName("Should get course by ID successfully")
        void shouldGetCourseById() {
            when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

            CourseDTO result = courseService.getCourseById(testTeacher, 1L);

            assertNotNull(result);
            assertEquals(testCourse.getName(), result.getName());
            verify(courseRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when course not found")
        void shouldThrowExceptionWhenCourseNotFound() {
            when(courseRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> courseService.getCourseById(testTeacher, 1L));
        }
    }

    @Nested
    @DisplayName("Create Course Tests")
    class CreateCourseTests {

        @Test
        @DisplayName("Should create course successfully")
        void shouldCreateCourse() {
            when(courseRepository.existsByCode(anyString())).thenReturn(false);
            when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

            CourseDTO result = courseService.createCourse(testCourseDTO);

            assertNotNull(result);
            assertEquals(testCourseDTO.getName(), result.getName());
            verify(courseRepository).save(any(Course.class));
        }

        @Test
        @DisplayName("Should throw exception when course code already exists")
        void shouldThrowExceptionWhenCodeExists() {
            when(courseRepository.existsByCode(anyString())).thenReturn(true);

            assertThrows(DuplicateResourceException.class,
                    () -> courseService.createCourse(testCourseDTO));
        }
    }

    @Nested
    @DisplayName("Update Course Tests")
    class UpdateCourseTests {

        @Test
        @DisplayName("Should update course successfully")
        void shouldUpdateCourse() {
            when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
            when(courseRepository.existsByCodeAndIdNot(anyString(), anyLong())).thenReturn(false);
            when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

            CourseDTO result = courseService.updateCourse(1L, testCourseDTO);

            assertNotNull(result);
            assertEquals(testCourseDTO.getName(), result.getName());
            verify(courseRepository).save(any(Course.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent course")
        void shouldThrowExceptionWhenUpdatingNonExistentCourse() {
            when(courseRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> courseService.updateCourse(1L, testCourseDTO));
        }
    }

    @Nested
    @DisplayName("Delete Course Tests")
    class DeleteCourseTests {

        @Test
        @DisplayName("Should delete course successfully")
        void shouldDeleteCourse() {
            when(courseRepository.existsById(1L)).thenReturn(true);

            courseService.deleteCourse(1L);

            verify(courseRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent course")
        void shouldThrowExceptionWhenDeletingNonExistentCourse() {
            when(courseRepository.existsById(1L)).thenReturn(false);

            assertThrows(ResourceNotFoundException.class,
                    () -> courseService.deleteCourse(1L));
        }
    }

    @Nested
    @DisplayName("Search Course Tests")
    class SearchCourseTests {

        @Test
        @DisplayName("Should search courses successfully")
        void shouldSearchCourses() {
            when(courseRepository.findAll(any(Specification.class)))
                    .thenReturn(Arrays.asList(testCourse));

            List<CourseDTO> results = courseService.searchCourses("test", "FALL", 2024);

            assertFalse(results.isEmpty());
            assertEquals(1, results.size());
            assertEquals(testCourse.getName(), results.get(0).getName());
        }
    }
}