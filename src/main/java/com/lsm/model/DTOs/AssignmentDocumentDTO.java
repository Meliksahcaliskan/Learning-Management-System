package com.lsm.model.DTOs;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentDocumentDTO {
    private Long id;

    @NotNull(message = "File name is required")
    @Size(max = 255, message = "File name must not exceed 255 characters")
    private String fileName;

    @NotNull(message = "File type is required")
    @Size(max = 50, message = "File type must not exceed 50 characters")
    private String fileType;

    @NotNull(message = "File size is required")
    @Positive(message = "File size must be positive")
    private Long fileSize;

    @NotNull(message = "Upload time is required")
    private LocalDateTime uploadTime;

    @NotNull(message = "Uploader username is required")
    private String uploadedByUsername;

    private boolean isTeacherUpload;
}
