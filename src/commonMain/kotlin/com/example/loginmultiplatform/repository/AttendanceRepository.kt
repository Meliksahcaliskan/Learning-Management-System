package com.example.loginmultiplatform.repository

import com.example.loginmultiplatform.network.ApiClient
import com.example.loginmultiplatform.model.AttendanceResponse
import com.example.loginmultiplatform.model.AttendanceStatsResponse
import com.example.loginmultiplatform.network.ApiService

class AttendanceRepository {
    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    suspend fun fetchAttendance(
        studentId: Int,
        startDate: String,
        endDate: String
    ): List<AttendanceResponse> {
        println("StudentId in attendance: $studentId")
        return apiService.getAttendance(studentId, startDate, endDate)
    }

    /*suspend fun fetchAttendanceStats(
        studentId: Int,
        classId: Int
    ): AttendanceStatsResponse {
        println("StudentId in attendance stats: $studentId, ClassId: $classId")
        return apiService.getAttendanceStats(studentId, classId)
    }*/
}