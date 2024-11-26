package com.lsm.service;

import com.lsm.model.DTOs.AnnouncementDTO;
import com.lsm.model.entity.Announcement;
import com.lsm.model.entity.ClassEntity;
import com.lsm.repository.AnnouncementRepository;
import com.lsm.repository.ClassEntityRepository;
import com.lsm.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final ClassEntityRepository classEntityRepository;

    public AnnouncementDTO createAnnouncement(AnnouncementDTO dto) {
        ClassEntity classEntity = classEntityRepository.findById(dto.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found"));

        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setClassEntity(classEntity);

        announcement = announcementRepository.save(announcement);
        return convertToDTO(announcement);
    }

    public List<AnnouncementDTO> getAnnouncementsByClassId(Long classId) {
        return announcementRepository.findByClassEntityId(classId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteAnnouncement(Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Announcement not found");
        }
        announcementRepository.deleteById(id);
    }

    private AnnouncementDTO convertToDTO(Announcement announcement) {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(announcement.getId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());
        dto.setClassId(announcement.getClassEntity().getId());
        dto.setCreatedAt(announcement.getCreatedAt());
        return dto;
    }
}