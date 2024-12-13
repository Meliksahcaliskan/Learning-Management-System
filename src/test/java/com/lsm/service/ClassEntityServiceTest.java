package com.lsm.service;

import com.lsm.model.entity.ClassEntity;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.StudentDetails;
import com.lsm.model.entity.TeacherDetails;
import com.lsm.model.entity.enums.Role;
import com.lsm.repository.AppUserRepository;
import com.lsm.repository.ClassEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import jakarta.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClassEntityServiceTest {

    @Mock
    private ClassEntityRepository classRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ClassEntityService classEntityService;

    private AppUser testTeacher;
    private AppUser testStudent;
    private ClassEntity testClass;
    private Set<AppUser> testStudents;

    @BeforeEach
    void setUp() {
        // Set up teacher
        testTeacher = AppUser.builder()
                .id(1L)
                .role(Role.ROLE_TEACHER)
                .teacherDetails(TeacherDetails.builder()
                        .classes(new HashSet<>())
                        .build())
                .build();

        // Set up student
        testStudent = AppUser.builder()
                .id(2L)
                .role(Role.ROLE_STUDENT)
                .studentDetails(StudentDetails.builder()
                        .classEntity(1L)
                        .build())
                .build();

        // Set up students set
        testStudents = new HashSet<>();
        testStudents.add(testStudent);

        // Set up class
        testClass = ClassEntity.builder()
                .id(1L)
                .name("Test Class")
                .description("Test Description")
                .teacher(testTeacher)
                .students(testStudents)
                .build();

        testTeacher.getTeacherDetails().getClasses().add(testClass);
    }

    @Nested
    @DisplayName("Create Class Tests")
    class CreateClassTests {

        @Test
        @DisplayName("Should create class successfully")
        void shouldCreateClass() throws AccessDeniedException {
            when(appUserRepository.findById(1L)).thenReturn(Optional.of(testTeacher));
            when(classRepository.save(any(ClassEntity.class))).thenReturn(testClass);

            ClassEntity result = classEntityService.createClass(testTeacher, testClass, 1L,
                    Collections.singletonList(2L));

            assertNotNull(result);
            assertEquals(testClass.getName(), result.getName());
            verify(classRepository).save(any(ClassEntity.class));
        }

        @Test
        @DisplayName("Should throw exception when teacher not found")
        void shouldThrowExceptionWhenTeacherNotFound() {
            when(appUserRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> classEntityService.createClass(testTeacher, testClass, 1L, null));
        }
    }

    @Nested
    @DisplayName("Get Class Tests")
    class GetClassTests {

        @Test
        @DisplayName("Should get class by ID for teacher successfully")
        void shouldGetClassByIdForTeacher() throws AccessDeniedException {
            when(classRepository.findById(1L)).thenReturn(Optional.of(testClass));

            ClassEntity result = classEntityService.getClassById(testTeacher, 1L);

            assertNotNull(result);
            assertEquals(testClass.getName(), result.getName());
            verify(classRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when class not found")
        void shouldThrowExceptionWhenClassNotFound() {
            when(classRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> classEntityService.getClassById(testTeacher, 1L));
        }
    }

    @Nested
    @DisplayName("Update Class Tests")
    class UpdateClassTests {

        @Test
        @DisplayName("Should update class successfully")
        void shouldUpdateClass() throws AccessDeniedException {
            when(classRepository.findById(1L)).thenReturn(Optional.of(testClass));
            when(classRepository.save(any(ClassEntity.class))).thenReturn(testClass);

            ClassEntity result = classEntityService.updateClass(testTeacher, 1L, testClass, 1L,
                    Collections.singletonList(2L));

            assertNotNull(result);
            assertEquals(testClass.getName(), result.getName());
            verify(classRepository).save(any(ClassEntity.class));
        }
    }

    @Nested
    @DisplayName("Student Management Tests")
    class StudentManagementTests {

        @Test
        @DisplayName("Should add student to class successfully")
        void shouldAddStudentToClass() throws AccessDeniedException {
            when(classRepository.findById(1L)).thenReturn(Optional.of(testClass));
            when(appUserRepository.findById(2L)).thenReturn(Optional.of(testStudent));
            when(classRepository.save(any(ClassEntity.class))).thenReturn(testClass);

            ClassEntity result = classEntityService.addStudent(testTeacher, 1L, 2L);

            assertNotNull(result);
            assertTrue(result.getStudents().contains(testStudent));
            verify(classRepository).save(any(ClassEntity.class));
        }

        @Test
        @DisplayName("Should remove student from class successfully")
        void shouldRemoveStudentFromClass() throws AccessDeniedException {
            when(classRepository.findById(1L)).thenReturn(Optional.of(testClass));
            when(appUserRepository.findById(2L)).thenReturn(Optional.of(testStudent));
            when(classRepository.save(any(ClassEntity.class))).thenReturn(testClass);

            ClassEntity result = classEntityService.removeStudent(testTeacher, 1L, 2L);

            assertNotNull(result);
            verify(classRepository).save(any(ClassEntity.class));
        }
    }

    @Nested
    @DisplayName("Get Classes By Role Tests")
    class GetClassesByRoleTests {

        @Test
        @DisplayName("Should get teacher's classes successfully")
        void shouldGetTeacherClasses() throws AccessDeniedException {
            when(authentication.getPrincipal()).thenReturn(testTeacher);
            when(classRepository.findClassesByTeacherId(1L))
                    .thenReturn(Collections.singletonList(testClass));

            List<ClassEntity> result = classEntityService.getTeacherClasses(authentication);

            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            assertEquals(testClass.getName(), result.get(0).getName());
        }

        @Test
        @DisplayName("Should get student's class successfully")
        void shouldGetStudentClass() throws AccessDeniedException {
            when(authentication.getPrincipal()).thenReturn(testStudent);
            when(classRepository.getClassEntityById(1L)).thenReturn(Optional.of(testClass));

            ClassEntity result = classEntityService.getStudentClasses(authentication);

            assertNotNull(result);
            assertEquals(testClass.getName(), result.getName());
        }
    }
}