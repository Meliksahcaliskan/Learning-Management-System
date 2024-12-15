package com.lsm.model.entity;

import com.lsm.model.validation.constraint.TCConstraint;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDetails {
    private String phone;
    @TCConstraint
    private String tc;
    private LocalDate birthDate;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "teacher_classes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "class_id")
    )
    @Builder.Default private Set<ClassEntity> classes = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "teacher_courses",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @Builder.Default private Set<Course> courses = new HashSet<>();
}
