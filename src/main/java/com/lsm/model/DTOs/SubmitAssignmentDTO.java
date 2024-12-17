package com.lsm.model.DTOs;

import lombok.*;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitAssignmentDTO {
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String submissionComment;
    private MultipartFile document;
}
