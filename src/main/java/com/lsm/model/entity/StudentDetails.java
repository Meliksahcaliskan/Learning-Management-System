package com.lsm.model.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.lsm.model.validation.constraint.TCConstraint;

import jakarta.persistence.*;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetails {
    @Column(name = "phone")
    private String phone;

    @Column(name = "tc")
    @TCConstraint
    private String tc;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "parent_name")
    private String parentName;

    @Column(name = "parent_phone")
    private String parentPhone;

    @ElementCollection
    @CollectionTable(name = "user_classes", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "class_id")
    @Builder.Default
    private List<Long> classes = new ArrayList<>();
}
