package com.lsm.model.DTOs;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.AssignmentDocument;
import com.lsm.model.entity.enums.AssignmentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentDTO {
    private Long id;

    private String title;
    private String description;
    private LocalDate dueDate;
    private String message;
    private AssignmentDocumentDTO teacherDocuments;
    private AssignmentDocumentDTO studentSubmissions;
    private Double grade;
    private String feedback;
    private LocalDate createdDate;
    private String assignedByTeacherName;
    private String className;
    private String courseName;
    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;


    public AssignmentDTO(Assignment assignment, String message) {
        this.id = assignment.getId();
        this.title = assignment.getTitle();
        this.description = assignment.getDescription();
        this.dueDate = assignment.getDueDate();
        this.message = message;
        this.teacherDocuments = (assignment.getTeacherDocument() == null) ? null : convertToDTO(assignment.getTeacherDocument());
        this.studentSubmissions = (assignment.getStudentSubmission() == null) ? null : convertToDTO(assignment.getStudentSubmission());
        this.grade = assignment.getGrade();
        this.feedback = assignment.getFeedback();
        this.createdDate = assignment.getDate();
        this.assignedByTeacherName = assignment.getAssignedBy().getUsername();
        this.className = assignment.getClassEntity().getName();
        this.courseName = assignment.getCourse().getName();
        this.status = assignment.getStatus();
    }

    private AssignmentDocumentDTO convertToDTO(AssignmentDocument doc) {
        return AssignmentDocumentDTO.builder()
                .assignmentId(id)
                .fileName(doc.getFileName())
                .fileType(doc.getFileType())
                .fileSize(doc.getFileSize())
                .uploadTime(doc.getUploadTime())
                .uploadedByUsername(doc.getUploadedBy().getUsername())
                .isTeacherUpload(doc.isTeacherUpload())
                .build();
    }
}
