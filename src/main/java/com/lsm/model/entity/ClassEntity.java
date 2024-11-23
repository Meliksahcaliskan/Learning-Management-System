package com.lsm.model.entity;

import java.util.List;

import com.lsm.model.entity.base.AppUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "classes")
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private AppUser teacher;

    @ManyToMany
    @JoinTable(
        name = "class_students",
        joinColumns = @JoinColumn(name = "class_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<AppUser> students;

    @OneToMany(mappedBy = "classEntity", cascade = CascadeType.ALL)
    private List<Assignment> assignments;

    // Default constructor
    public ClassEntity() {
    }

    // Parameterized constructor
    public ClassEntity(String name, String description, AppUser teacher, List<AppUser> students) {
        this.name = name;
        this.description = description;
        this.teacher = teacher;
        this.students = students;
    }
}

