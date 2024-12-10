package com.example.loginmultiplatform.model

import kotlinx.datetime.LocalDate

data class ResponseWrapper<T>(
    val success: Boolean,
    val message: String,
    val data: T
)

data class AttendanceResponse(
    val id: Long,
    val studentId: Long,
    val studentName: String,
    val studentSurname: String,
    val date: String,
    val status: String, //"PRESENT", "ABSENT", or "LATE"
    val comment: String?,
    val classId: Long,
    val courseId: Long
)

data class AttendanceStatsResponse(
    val studentId: Long,
    val studentName: String,
    val classId: Long,
    val className: String,
    val courseId: Long,
    val courseName: String,
    val attendancePercentage: Int,
    val totalClasses: Int,
    val presentCount: Int,
    val absentCount: Int,
    val lateCount: Int,
    val recentAttendance: List<AttendanceResponse>
)