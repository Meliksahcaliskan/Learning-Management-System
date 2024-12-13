package com.lsm.service;

import com.lsm.exception.ResourceNotFoundException;
import com.lsm.model.DTOs.AnnouncementDTO;
import com.lsm.model.entity.Announcement;
import com.lsm.model.entity.ClassEntity;
import com.lsm.model.entity.TeacherDetails;
import com.lsm.model.entity.StudentDetails;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import com.lsm.repository.AnnouncementRepository;
import com.lsm.repository.ClassEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnouncementServiceTest {

    @Mock
    private AnnouncementRepository announcementRepository;

    @Mock
    private ClassEntityRepository classEntityRepository;

    @Mock
    private AppUserService appUserService;

    @InjectMocks
    private AnnouncementService announcementService;

    private AppUser teacherUser;
    private AppUser studentUser;
    private AppUser adminUser;
    private ClassEntity classEntity;
    private Announcement announcement;
    private AnnouncementDTO announcementDTO;

    @BeforeEach
    void setUp() {
        // Setup Class
        classEntity = ClassEntity.builder()
                .id(1L)
                .name("Test Class")
                .build();

        // Setup Teacher
        teacherUser = AppUser.builder()
                .id(1L)
                .role(Role.ROLE_TEACHER)
                .teacherDetails(TeacherDetails.builder()
                        .classes(new HashSet<>(Collections.singletonList(classEntity)))
                        .build())
                .build();

        // Setup Student
        studentUser = AppUser.builder()
                .id(2L)
                .role(Role.ROLE_STUDENT)
                .studentDetails(StudentDetails.builder()
                        .classEntity(1L)
                        .build())
                .build();

        // Setup Admin
        adminUser = AppUser.builder()
                .id(3L)
                .role(Role.ROLE_ADMIN)
                .build();

        // Setup Announcement
        announcement = new Announcement();
        announcement.setId(1L);
        announcement.setTitle("Test Announcement");
        announcement.setContent("Test Content");
        announcement.setClassEntity(classEntity);
        announcement.setCreatedAt(LocalDateTime.now());

        // Setup AnnouncementDTO
        announcementDTO = new AnnouncementDTO();
        announcementDTO.setId(1L);
        announcementDTO.setTitle("Test Announcement");
        announcementDTO.setContent("Test Content");
        announcementDTO.setClassId(1L);
        announcementDTO.setCreatedAt(LocalDate.now());
    }

    @Nested
    @DisplayName("Create Announcement Tests")
    class CreateAnnouncementTests {

        @Test
        @DisplayName("Should allow teacher to create announcement for their class")
        void shouldAllowTeacherToCreateAnnouncement() throws AccessDeniedException {
            // Arrange
            when(appUserService.getCurrentUserWithDetails(teacherUser.getId())).thenReturn(teacherUser);
            when(classEntityRepository.findById(1L)).thenReturn(Optional.of(classEntity));
            when(announcementRepository.save(any(Announcement.class))).thenReturn(announcement);

            // Act
            AnnouncementDTO result = announcementService.createAnnouncement(teacherUser, announcementDTO);

            // Assert
            assertNotNull(result);
            assertEquals(announcement.getTitle(), result.getTitle());
            verify(announcementRepository).save(any(Announcement.class));
        }

        @Test
        @DisplayName("Should not allow student to create announcement")
        void shouldNotAllowStudentToCreateAnnouncement() {
            // Arrange
            when(appUserService.getCurrentUserWithDetails(studentUser.getId())).thenReturn(studentUser);

            // Act & Assert
            assertThrows(AccessDeniedException.class,
                    () -> announcementService.createAnnouncement(studentUser, announcementDTO));
        }

        @Test
        @DisplayName("Should not allow teacher to create announcement for other class")
        void shouldNotAllowTeacherToCreateAnnouncementForOtherClass() {
            // Arrange
            ClassEntity otherClass = ClassEntity.builder()
                    .id(2L)
                    .name("Other Class")
                    .build();
            announcementDTO.setClassId(2L);

            when(appUserService.getCurrentUserWithDetails(teacherUser.getId())).thenReturn(teacherUser);
            when(classEntityRepository.findById(2L)).thenReturn(Optional.of(otherClass));

            // Act & Assert
            assertThrows(AccessDeniedException.class,
                    () -> announcementService.createAnnouncement(teacherUser, announcementDTO));
        }
    }

    @Nested
    @DisplayName("Get Announcements Tests")
    class GetAnnouncementsTests {

        @Test
        @DisplayName("Should allow teacher to get announcements for their class")
        void shouldAllowTeacherToGetAnnouncements() throws AccessDeniedException {
            // Arrange
            when(appUserService.getCurrentUserWithDetails(teacherUser.getId())).thenReturn(teacherUser);
            when(announcementRepository.findByClassEntityId(1L))
                    .thenReturn(Collections.singletonList(announcement));

            // Act
            List<AnnouncementDTO> results = announcementService.getAnnouncementsByClassId(teacherUser, 1L);

            // Assert
            assertFalse(results.isEmpty());
            assertEquals(1, results.size());
            assertEquals(announcement.getTitle(), results.get(0).getTitle());
        }

        @Test
        @DisplayName("Should allow student to get announcements for their class")
        void shouldAllowStudentToGetAnnouncements() throws AccessDeniedException {
            // Arrange
            when(appUserService.getCurrentUserWithDetails(studentUser.getId())).thenReturn(studentUser);
            when(announcementRepository.findByClassEntityId(1L))
                    .thenReturn(Collections.singletonList(announcement));

            // Act
            List<AnnouncementDTO> results = announcementService.getAnnouncementsByClassId(studentUser, 1L);

            // Assert
            assertFalse(results.isEmpty());
            assertEquals(1, results.size());
            assertEquals(announcement.getTitle(), results.get(0).getTitle());
        }
    }

    @Nested
    @DisplayName("Update Announcement Tests")
    class UpdateAnnouncementTests {

        @Test
        @DisplayName("Should allow teacher to update their announcement")
        void shouldAllowTeacherToUpdateAnnouncement() throws AccessDeniedException {
            // Arrange
            when(appUserService.getCurrentUserWithDetails(teacherUser.getId())).thenReturn(teacherUser);
            when(announcementRepository.findById(1L)).thenReturn(Optional.of(announcement));
            when(announcementRepository.save(any(Announcement.class))).thenReturn(announcement);

            // Act
            AnnouncementDTO result = announcementService.updateAnnouncement(teacherUser, 1L, announcementDTO);

            // Assert
            assertNotNull(result);
            assertEquals(announcementDTO.getTitle(), result.getTitle());
            verify(announcementRepository).save(any(Announcement.class));
        }

        @Test
        @DisplayName("Should throw exception when announcement not found")
        void shouldThrowExceptionWhenAnnouncementNotFound() {
            // Arrange
            when(appUserService.getCurrentUserWithDetails(teacherUser.getId())).thenReturn(teacherUser);
            when(announcementRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(EntityNotFoundException.class,
                    () -> announcementService.updateAnnouncement(teacherUser, 1L, announcementDTO));
        }
    }

    @Nested
    @DisplayName("Delete Announcement Tests")
    class DeleteAnnouncementTests {

        @Test
        @DisplayName("Should allow teacher to delete their announcement")
        void shouldAllowTeacherToDeleteAnnouncement() throws AccessDeniedException {
            // Arrange
            when(appUserService.getCurrentUserWithDetails(teacherUser.getId())).thenReturn(teacherUser);
            when(announcementRepository.findById(1L)).thenReturn(Optional.of(announcement));
            when(announcementRepository.existsById(1L)).thenReturn(true);

            // Act
            announcementService.deleteAnnouncement(teacherUser, 1L);

            // Assert
            verify(announcementRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent announcement")
        void shouldThrowExceptionWhenDeletingNonExistentAnnouncement() {
            // Arrange
            when(appUserService.getCurrentUserWithDetails(teacherUser.getId())).thenReturn(teacherUser);
            when(announcementRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> announcementService.deleteAnnouncement(teacherUser, 1L));
        }
    }
}