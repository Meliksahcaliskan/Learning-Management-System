package com.example.loginmultiplatform.repository

import com.example.loginmultiplatform.model.ResponseWrapper
import com.example.loginmultiplatform.model.TeacherClassResponse
import com.example.loginmultiplatform.network.ApiClient
import com.example.loginmultiplatform.network.ApiService

class TeacherAttendanceRepository {
    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    suspend fun getTeacherClasses(): ResponseWrapper<List<TeacherClassResponse>> {
        val response = apiService.fetchTeacherClasses()

        if (response.success) {
            return response
        } else {
            throw Exception(response.message)
        }
    }
}

