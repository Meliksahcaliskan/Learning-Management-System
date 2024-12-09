package com.example.loginmultiplatform.model

import kotlinx.datetime.LocalDate

data class AttendanceResponse(
    val id: Long,
    val studentId: Long,
    val studentName: String,
    val studentSurname: String,
    val date: String,
    val status: String, //"PRESENT", "ABSENT", or "LATE"
    val comment: String?
)

data class AttendanceStatsResponse(
    val studentId: Long,
    val classId: Long,
    val attendancePercentage: Int,
    val totalClasses: Int,
    val presentCount: Int,
    val absentCount: Int,
    val lateCount: Int,
    val recentAttendance: List<AttendanceResponse>
)