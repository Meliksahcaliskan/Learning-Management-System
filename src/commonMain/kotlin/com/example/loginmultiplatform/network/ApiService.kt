package com.example.loginmultiplatform.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("user")
    suspend fun getUser(@Query("id") userId: String): UserResponse
}
