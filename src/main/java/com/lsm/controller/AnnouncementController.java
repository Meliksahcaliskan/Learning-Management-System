package com.lsm.controller;

import com.lsm.model.DTOs.AnnouncementDTO;
import com.lsm.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<AnnouncementDTO> createAnnouncement(@RequestBody AnnouncementDTO announcementDTO) {
        return ResponseEntity.ok(announcementService.createAnnouncement(announcementDTO));
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<List<AnnouncementDTO>> getAnnouncementsByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(announcementService.getAnnouncementsByClassId(classId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok().build();
    }
}