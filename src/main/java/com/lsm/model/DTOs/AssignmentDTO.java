package com.lsm.model.DTOs;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.AssignmentDocument;
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
    private List<StudentSubmissionDTO> studentSubmissions;
    private LocalDate createdDate;
    private String assignedByTeacherName;
    private Long classId;
    private String className;
    private Long courseId;
    private String courseName;
    private LocalDate lastModified;
    private String lastModifiedByUsername;

    public AssignmentDTO(Assignment assignment, String message) {
        this.id = assignment.getId();
        this.title = assignment.getTitle();
        this.description = assignment.getDescription();
        this.dueDate = assignment.getDueDate();
        this.message = message;
        this.teacherDocuments = convertToDTO(assignment.getTeacherDocument());
        this.studentSubmissions = assignment.getStudentSubmissions() != null
                ? assignment.getStudentSubmissions().stream()
                .map(StudentSubmissionDTO::new)
                .collect(Collectors.toList())
                : new ArrayList<>();
        this.createdDate = assignment.getDate();
        this.assignedByTeacherName = assignment.getAssignedBy().getUsername();
        this.classId = assignment.getClassEntity().getId();
        this.className = assignment.getClassEntity().getName();
        this.courseId = assignment.getCourse().getId();
        this.courseName = assignment.getCourse().getName();
        this.lastModified = assignment.getLastModified();
        this.lastModifiedByUsername = assignment.getLastModifiedBy().getUsername();
    }

    private AssignmentDocumentDTO convertToDTO(AssignmentDocument doc) {
        if (doc == null)
            return null;
        return AssignmentDocumentDTO.builder()
                .assignmentId(doc.getAssignment() != null ? doc.getAssignment().getId() : null)
                .documentId(doc.getId())
                .fileName(doc.getFileName())
                .fileType(doc.getFileType())
                .fileSize(doc.getFileSize())
                .uploadTime(doc.getUploadTime())
                .uploadedByUsername(doc.getUploadedBy() != null ? doc.getUploadedBy().getUsername() : null)
                .build();
    }
}
