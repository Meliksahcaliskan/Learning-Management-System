package com.lsm.model.entity;

import java.time.LocalDate;
import java.util.List;

import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.AssignmentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "assigned_by_teacher_id", nullable = false)
    private AppUser assignedBy;  // Reference to the teacher who created the assignment

    @ManyToMany
    @JoinTable(
        name = "assignment_student",
        joinColumns = @JoinColumn(name = "assignment_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<AppUser> assignedTo;  // List of students who received the assignment

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AssignmentStatus status = AssignmentStatus.PENDING;  // Default status as PENDING

    @NotNull
    @Column(name = "class_id", nullable = false)
    private Long classId;

    @NotNull
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @NotNull
    @Column(name = "assignment_date", nullable = false)
    private LocalDate date;

    // Constructors
    public Assignment() {}

    public Assignment(String title, String description, LocalDate dueDate, AppUser assignedBy, 
                      List<AppUser> assignedTo, Long classId, Long courseId, LocalDate date) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.assignedBy = assignedBy;
        this.assignedTo = assignedTo;
        this.classId = classId;
        this.courseId = courseId;
        this.date = date;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public AppUser getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(AppUser assignedBy) {
        this.assignedBy = assignedBy;
    }

    public List<AppUser> getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(List<AppUser> assignedTo) {
        this.assignedTo = assignedTo;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
