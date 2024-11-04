package com.lsm.model.DTOs;

import java.time.LocalDate;

import com.lsm.model.entity.enums.AttendanceStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentSurname;
    private LocalDate date;
    private AttendanceStatus status;
    private String comment;
    private Long classId;
}
