package com.lsm.model.DTOs.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class StudentRegisterRequestDTO extends RegisterRequestDTO {
    private String phone;
    private String tc;
    private LocalDate birthDate;
    private LocalDate registrationDate;
    private String parentName;
    private String parentPhone;
    private Long classEntity;
}
