package com.lsm.model.entity;

import java.time.LocalDate;

import com.lsm.model.validation.constraint.TCConstraint;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class StudentDetails {
    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

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

    // Getters and Setters

    public StudentDetails(String name, String surname, String tc, LocalDate birthDate, LocalDate registrationDate, String parentName, String parentPhone) {
        this.name = name;
        this.surname = surname;
        this.tc = tc;
        this.birthDate = birthDate;
        this.registrationDate = registrationDate;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getTc() { return tc; }
    public void setTc(String tc) { this.tc = tc; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }
}
