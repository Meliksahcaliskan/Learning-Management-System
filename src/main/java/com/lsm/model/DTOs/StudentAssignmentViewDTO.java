package com.lsm.model.DTOs;

import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.AssignmentDocument;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAssignmentViewDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private AssignmentDocumentDTO teacherDocument;
    private StudentSubmissionDTO mySubmission;  // Only the current student's submission
    private LocalDate createdDate;
    private String assignedByTeacherName;
    private String className;
    private String courseName;

    public StudentAssignmentViewDTO(Assignment assignment, Long studentId) {
        this.id = assignment.getId();
        this.title = assignment.getTitle();
        this.description = assignment.getDescription();
        this.dueDate = assignment.getDueDate();
        this.teacherDocument = convertToDTO(assignment.getTeacherDocument());
        this.createdDate = assignment.getDate();
        this.assignedByTeacherName = assignment.getAssignedBy().getUsername();
        this.className = assignment.getClassEntity().getName();
        this.courseName = assignment.getCourse().getName();

        // Find and set only the current student's submission
        this.mySubmission = assignment.getStudentSubmissions().stream()
                .filter(submission -> submission.getStudent().getId().equals(studentId))
                .findFirst()
                .map(StudentSubmissionDTO::new)
                .orElse(null);
    }

    private AssignmentDocumentDTO convertToDTO(AssignmentDocument doc) {
        if (doc == null)
            return null;
        return AssignmentDocumentDTO.builder()
                .assignmentId(doc.getAssignment().getId())
                .documentId(doc.getId())
                .fileName(doc.getFileName())
                .fileType(doc.getFileType())
                .fileSize(doc.getFileSize())
                .uploadTime(doc.getUploadTime())
                .uploadedByUsername(doc.getUploadedBy().getUsername())
                .build();
    }
}
