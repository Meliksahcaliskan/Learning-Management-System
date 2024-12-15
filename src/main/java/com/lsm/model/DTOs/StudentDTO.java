package com.lsm.model.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    private String username;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String tc;
    private LocalDate birthDate;
    private LocalDate registrationDate;
    private String parentName;
    private String parentPhone;
    private Long classEntityId;
}
