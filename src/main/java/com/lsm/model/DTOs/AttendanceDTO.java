package com.lsm.model.DTOs;

import java.time.LocalDate;

import com.lsm.model.entity.Attendance;
import com.lsm.model.entity.enums.AttendanceStatus;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private Long attendanceId;
    private Long studentId;
    private String studentName;
    private String studentSurname;
    private LocalDate date;
    private AttendanceStatus status;
    private String comment;
    private Long classId;
    private Long courseId;

    public AttendanceDTO(Attendance attendance, String comment) {
        this.attendanceId = attendance.getId();
        this.studentId = attendance.getStudent().getId();
        this.studentName = attendance.getStudent().getName();
        this.studentSurname = attendance.getStudent().getSurname();
        this.date = attendance.getDate();
        this.status = attendance.getStatus();
        this.comment = comment;
        this.classId = attendance.getClassId();
        this.courseId = attendance.getCourseId();
    }
}
