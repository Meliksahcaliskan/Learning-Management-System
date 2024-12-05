package com.lsm.model.DTOs;

import com.lsm.model.entity.StudentSubmission;
import com.lsm.model.entity.enums.AssignmentStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSubmissionDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private AssignmentStatus status;
    private AssignmentDocumentDTO document;
    private LocalDate submissionDate;
    private String comment;
    private Double grade;
    private String feedback;

    public StudentSubmissionDTO(StudentSubmission submission) {
        this.id = submission.getId();
        this.studentId = submission.getStudent().getId();
        this.studentName = submission.getStudent().getUsername();
        this.status = submission.getStatus();
        this.submissionDate = submission.getSubmissionDate();
        this.comment = submission.getComment();
        this.grade = submission.getGrade();
        this.feedback = submission.getFeedback();

        if (submission.getDocument() != null) {
            this.document = AssignmentDocumentDTO.builder()
                    .assignmentId(submission.getAssignment().getId())
                    .fileName(submission.getDocument().getFileName())
                    .fileType(submission.getDocument().getFileType())
                    .fileSize(submission.getDocument().getFileSize())
                    .uploadTime(submission.getDocument().getUploadTime())
                    .uploadedByUsername(submission.getDocument().getUploadedBy().getUsername())
                    .build();
        }
    }
}
