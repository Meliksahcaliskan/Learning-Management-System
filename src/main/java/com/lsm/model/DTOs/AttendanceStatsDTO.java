package com.lsm.model.DTOs;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttendanceStatsDTO {
    private Long studentId;
    private Long classId;
    private double attendancePercentage;
    private int totalClasses;
    private int presentCount;
    private int absentCount;
    private int lateCount;
    private List<AttendanceDTO> recentAttendance;
}
