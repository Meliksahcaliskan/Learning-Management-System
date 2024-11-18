package com.example.loginmultiplatform.repository

import com.example.loginmultiplatform.network.ApiClient
import com.example.loginmultiplatform.network.ApiService
import com.example.loginmultiplatform.network.LoginRequest

class UserRepository {
    private val apiService = ApiClient.retrofit.create(ApiService::class.java)

    suspend fun login(email: String, password: String) = apiService.login(LoginRequest(email, password))

    suspend fun getUser(userId: String) = apiService.getUser(userId)
}