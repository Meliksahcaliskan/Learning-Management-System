package com.lsm.service;

import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.AssignmentDocument;
import com.lsm.model.entity.base.AppUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.file-storage.base-path}")
    private String baseStoragePath;

    public AssignmentDocument handleDocumentUpload(MultipartFile file, Assignment assignment, AppUser uploader) throws IOException {
        // Generate unique file name to prevent collisions
        String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());

        // Create directory if it doesn't exist
        String relativePath = String.format("assignments/%d/%s", assignment.getId(), uniqueFileName);
        Path fullPath = Paths.get(baseStoragePath, relativePath);
        Files.createDirectories(fullPath.getParent());

        // Save file to storage
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, fullPath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Create and return AssignmentDocument entity
        return AssignmentDocument.builder()
                .fileName(file.getOriginalFilename())
                .filePath(relativePath)
                .uploadTime(LocalDateTime.now())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .assignment(assignment)
                .uploadedBy(uploader)
                .build();
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = FilenameUtils.getExtension(originalFileName);
        String baseName = FilenameUtils.getBaseName(originalFileName);
        return String.format("%s_%s.%s",
                baseName,
                UUID.randomUUID().toString().substring(0, 8),
                extension);
    }
}
