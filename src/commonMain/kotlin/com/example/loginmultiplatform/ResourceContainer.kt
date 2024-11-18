// File: src/commonMain/kotlin/com/example/loginmultiplatform/ResourceContainer.kt

package com.example.loginmultiplatform

interface ResourceContainer {
    val lighthouse: Int      // Changed from Any to Int
    val appLogo: Int         // Changed from Any to Int
    val welcomeAgain: String
    val emailPlaceholder: String
    val passwordPlaceholder: String
    val rememberMe: String
    val forgotPassword: String
    val signIn: String
}

expect fun getPlatformResourceContainer(): ResourceContainer
