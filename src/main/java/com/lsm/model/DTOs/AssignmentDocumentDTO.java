package com.lsm.model.DTOs;

import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.AssignmentDocument;
import com.lsm.model.entity.base.AppUser;
import com.lsm.repository.AppUserRepository;
import com.lsm.repository.AssignmentRepository;
import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentDocumentDTO {
    @NotNull(message = "Id of which assignment this document belongs to")
    private Long assignmentId;

    @NotNull(message = "File name is required")
    @Size(max = 255, message = "File name must not exceed 255 characters")
    private String fileName;

    @NotNull(message = "File type is required")
    @Size(max = 50, message = "File type must not exceed 50 characters")
    private String fileType;

    private String filePath;

    @NotNull(message = "File size is required")
    @Positive(message = "File size must be positive")
    private Long fileSize;

    @NotNull(message = "Upload time is required")
    private LocalDateTime uploadTime;

    @NotNull(message = "Uploader username is required")
    private String uploadedByUsername;

    public AssignmentDocument DTOtoDocument(AssignmentRepository assignmentRepository,
                                            AppUserRepository appUserRepository) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found with id: " + assignmentId));

        AppUser uploader = appUserRepository.findByUsername(uploadedByUsername)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + uploadedByUsername));

        return AssignmentDocument.builder()
                .assignment(assignment)
                .fileSize(fileSize)
                .uploadTime(uploadTime)
                .fileType(fileType)
                .fileName(fileName)
                .filePath(filePath)
                .uploadedBy(uploader)
                .build();
    }
}
