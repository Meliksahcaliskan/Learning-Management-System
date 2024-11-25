package com.lsm.model.entity;

import com.lsm.model.validation.constraint.TCConstraint;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection
    @CollectionTable(name = "teacher_classes", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "class_id")
    private List<Long> classes = new ArrayList<>();
}

