package com.lsm.model.DTOs;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AnnouncementDTO {
    private Long id;
    private String title;
    private String content;
    private Long classId;
    private LocalDateTime createdAt;
}

