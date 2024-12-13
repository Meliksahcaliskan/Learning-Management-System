package com.lsm.model.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AnnouncementDTO {
    private Long id;
    @NotNull private String title;
    @NotNull private String content;
    @NotNull private Long classId;
    private LocalDate createdAt;
}

