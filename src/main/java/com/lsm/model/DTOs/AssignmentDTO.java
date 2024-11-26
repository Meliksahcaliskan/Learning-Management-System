package com.lsm.model.DTOs;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.lsm.model.entity.Assignment;
import com.lsm.model.entity.AssignmentDocument;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignmentDTO {
    private Long id; // Unique identifier for the assignment
    private String title;
    private String description;
    private LocalDate dueDate;
    private String message;
    private List<AssignmentDocumentDTO> teacherDocuments;
    private List<AssignmentDocumentDTO> studentSubmissions;
    private Double grade;
    private String feedback;

    // Default constructor
    // public AssignmentDTO() {}

    // Parameterized constructor
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
    }

    private AssignmentDocumentDTO convertToDTO(AssignmentDocument doc) {
        return new AssignmentDocumentDTO(
                doc.getId(),
                doc.getFileName(),
                doc.getFileType(),
                doc.getFileSize(),
                doc.getUploadTime(),
                doc.getUploadedBy().getUsername(),
                doc.isTeacherUpload()
        );
    }
}
