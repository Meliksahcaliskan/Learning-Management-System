package com.example.loginmultiplatform.repository

import com.example.loginmultiplatform.network.ApiClient
import com.example.loginmultiplatform.model.AttendanceResponse
import com.example.loginmultiplatform.model.AttendanceStatsResponse
import com.example.loginmultiplatform.model.ResponseWrapper
import com.example.loginmultiplatform.network.ApiService

class AttendanceRepository {
    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    suspend fun fetchAttendance(
        studentId: Int,
        startDate: String,
        endDate: String
    ): List<AttendanceResponse> {
        //println("StudentId in attendance: $studentId")
        val response = apiService.getAttendance(studentId, startDate, endDate)

        if (response.success) {
            return response.data
        } else {
            throw Exception(response.message)
        }
    }

    /*suspend fun fetchAttendanceStats(
        studentId: Int,
        classId: Int
    ): ResponseWrapper<AttendanceStatsResponse> {
        //println("StudentId in attendance stats: $studentId, ClassId: $classId")
        return apiService.getAttendanceStats(studentId, classId)
    }*/
}