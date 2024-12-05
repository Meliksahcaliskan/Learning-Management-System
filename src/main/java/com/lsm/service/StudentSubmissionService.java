package com.lsm.service;

import com.lsm.model.DTOs.SubmitAssignmentDTO;
import com.lsm.model.entity.*;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.AssignmentStatus;
import com.lsm.repository.AssignmentRepository;
import com.lsm.repository.ClassEntityRepository;
import com.lsm.repository.StudentSubmissionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentSubmissionService {

    private final StudentSubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final ClassEntityRepository classEntityRepository;

    @Value("${app.upload.dir}")
    private String UPLOAD_DIR;

    @Transactional
    public StudentSubmission submitAssignment(Long assignmentId, SubmitAssignmentDTO submitDTO, AppUser student)
            throws IOException {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));

        // Verify submission deadline
        validateSubmissionDeadline(assignment);

        // Verify student's class enrollment
        validateStudentEnrollment(student, assignment);

        // Get or create submission
        StudentSubmission submission = getOrCreateSubmission(assignment, student);

        // Validate submission status
        validateSubmissionStatus(submission);

        // Handle document upload
        handleDocumentUpload(submission, submitDTO.getDocument(), student);

        // Update submission details
        submission.setStatus(AssignmentStatus.SUBMITTED);
        submission.setSubmissionDate(LocalDate.now());
        submission.setComment(submitDTO.getSubmissionComment());

        return submissionRepository.save(submission);
    }

    private void validateSubmissionDeadline(Assignment assignment) {
        if (LocalDate.now().isAfter(assignment.getDueDate())) {
            throw new IllegalStateException("Assignment deadline has passed");
        }
    }

    private void validateStudentEnrollment(AppUser student, Assignment assignment) throws AccessDeniedException {
        ClassEntity studentClass = classEntityRepository.findById(student.getStudentDetails().getClassEntity())
                .orElseThrow(() -> new EntityNotFoundException("Student's class not found"));

        if (!studentClass.getCourses().contains(assignment.getCourse())) {
            throw new AccessDeniedException("You can only submit assignments for your enrolled courses");
        }
    }

    private StudentSubmission getOrCreateSubmission(Assignment assignment, AppUser student) {
        return assignment.getStudentSubmissions().stream()
                .filter(sub -> sub.getStudent().getId().equals(student.getId()))
                .findFirst()
                .orElse(StudentSubmission.builder()
                        .assignment(assignment)
                        .student(student)
                        .status(AssignmentStatus.PENDING)
                        .build());
    }

    private void validateSubmissionStatus(StudentSubmission submission) {
        if (submission.getStatus() == AssignmentStatus.SUBMITTED) {
            throw new IllegalStateException("You have already submitted this assignment");
        }
        if (submission.getStatus() == AssignmentStatus.GRADED || submission.getGrade() != null) {
            throw new IllegalStateException("This assignment has already been graded");
        }
    }

    private void handleDocumentUpload(StudentSubmission submission, MultipartFile file, AppUser student)
            throws IOException {
        // Delete existing document if present
        if (submission.getDocument() != null) {
            Files.deleteIfExists(Paths.get(submission.getDocument().getFilePath()));
            submission.setDocument(null);
        }

        // Create new document
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(UPLOAD_DIR);
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        AssignmentDocument document = AssignmentDocument.builder()
                .fileName(file.getOriginalFilename())
                .filePath(filePath.toString())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .uploadTime(LocalDateTime.now())
                .uploadedBy(student)
                .assignment(submission.getAssignment())
                .build();

        submission.setDocument(document);
    }

    public StudentSubmission findSubmissionById(Long submissionId) {
        return submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Submission not found"));
    }

    public StudentSubmission findStudentSubmission(Long assignmentId, Long studentId) {
        return submissionRepository.findByAssignment_IdAndStudent_Id(assignmentId, studentId)
                .orElseThrow(() -> new EntityNotFoundException("Submission not found"));
    }

    @Transactional
    public void deleteSubmission(Long submissionId) throws IOException {
        StudentSubmission submission = findSubmissionById(submissionId);
        if (submission.getDocument() != null) {
            Files.deleteIfExists(Paths.get(submission.getDocument().getFilePath()));
        }
        submissionRepository.delete(submission);
    }
}