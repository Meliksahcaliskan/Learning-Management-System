package com.lsm.model.DTOs;

import java.time.LocalDate;
import java.util.List;

import com.lsm.model.entity.base.AppUser;

public class AssignmentRequestDTO {
    private AppUser teacher;
    private String title;
    private String description;
    private LocalDate dueDate;
    private List<Long> studentIdList;

    // Default constructor
    public AssignmentRequestDTO() {
    }

    // Parameterized constructor
    public AssignmentRequestDTO(AppUser teacher, String title, String description, LocalDate dueDate, List<Long> studentIdList) {
        this.teacher = teacher;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.studentIdList = studentIdList;
    }

    // Getters and Setters
    public AppUser getTeacher() {
        return teacher;
    }

    public void setTeacher(AppUser teacher) {
        this.teacher = teacher;
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

    public List<Long> getStudentIdList() {
        return studentIdList;
    }

    public void setStudentIdList(List<Long> studentIdList) {
        this.studentIdList = studentIdList;
    }
}

