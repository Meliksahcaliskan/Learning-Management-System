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
    @Column(name = "phone")
    private String phone;

    @Column(name = "tc")
    @TCConstraint
    private String tc;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @ElementCollection
    @CollectionTable(name = "user_classes", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "class_id")
    @Builder.Default
    private List<Long> classes = new ArrayList<>();
}
