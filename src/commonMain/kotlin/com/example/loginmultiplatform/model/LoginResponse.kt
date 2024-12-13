package com.example.loginmultiplatform.model

data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val data: LoginData
)

data class LoginData(
    val id: Int,
    val username: String,
    val name: String,
    val surname: String,
    val email: String,
    val role: String,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
    val issuedAt: String,
    val tokenType: String
)