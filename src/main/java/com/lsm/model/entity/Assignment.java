package com.lsm.model.entity;

import java.time.LocalDate;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
    private AppUser assignedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AssignmentStatus status = AssignmentStatus.PENDING;

    @NotNull
    @Column(name = "class_id", nullable = false, insertable = false, updatable = false)  // Avoids duplicate mapping
    private Long classId;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)  // Maps to the ClassEntity relation
    private ClassEntity classEntity;

    @NotNull
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @NotNull
    @Column(name = "assignment_date", nullable = false)
    private LocalDate date;

    // Constructors
    public Assignment() {}

    public Assignment(String title, String description, LocalDate dueDate, AppUser assignedBy, 
                      Long classId, Long courseId, LocalDate date) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.assignedBy = assignedBy;
        this.classId = classId;
        this.courseId = courseId;
        this.date = date;
    }
}
