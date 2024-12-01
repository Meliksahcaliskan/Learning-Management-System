package com.lsm.model.DTOs;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.AssignmentDocument;
import com.lsm.model.entity.enums.AssignmentStatus;
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
    private List<AssignmentDocumentDTO> teacherDocuments;
    private List<AssignmentDocumentDTO> studentSubmissions;
    private Double grade;
    private String feedback;
    private LocalDate createdDate;
    private String assignedByTeacherName;
    private String className;
    private String courseName;
    private AssignmentStatus status; // New field for status

    public AssignmentDTO(Assignment assignment, String message) {
        this.id = assignment.getId();
        this.title = assignment.getTitle();
        this.description = assignment.getDescription();
        this.dueDate = assignment.getDueDate();
        this.message = message;
        this.teacherDocuments = assignment.getTeacherDocuments().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        this.studentSubmissions = assignment.getStudentSubmissions().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        this.grade = assignment.getGrade();
        this.feedback = assignment.getFeedback();
        this.createdDate = assignment.getDate();
        this.assignedByTeacherName = assignment.getAssignedBy().getUsername();
        this.className = assignment.getClassEntity().getName();
        this.courseName = assignment.getCourse().getName();
        this.status = assignment.getStatus(); // Set the status from the entity
    }

    private AssignmentDocumentDTO convertToDTO(AssignmentDocument doc) {
        return AssignmentDocumentDTO.builder()
                .id(doc.getId())
                .fileName(doc.getFileName())
                .fileType(doc.getFileType())
                .fileSize(doc.getFileSize())
                .uploadTime(doc.getUploadTime())
                .uploadedByUsername(doc.getUploadedBy().getUsername())
                .isTeacherUpload(doc.isTeacherUpload())
                .build();
    }
}
