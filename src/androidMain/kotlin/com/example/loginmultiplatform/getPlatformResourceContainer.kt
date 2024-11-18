package com.example.loginmultiplatform

//import com.example.loginmultiplatform.ResourceContainer
import android.content.Context

actual fun getPlatformResourceContainer(): ResourceContainer {
    val context = AppContext.context

    return object : ResourceContainer {
        override val lighthouse: Int = R.drawable.lighthouse
        override val appLogo: Int = R.drawable.app_logo
        override val welcomeAgain: String = context.getString(R.string.welcome_again)
        override val emailPlaceholder: String = context.getString(R.string.email_placeholder)
        override val passwordPlaceholder: String = context.getString(R.string.password_placeholder)
        override val rememberMe: String = context.getString(R.string.remember_me)
        override val forgotPassword: String = context.getString(R.string.forgot_password)
        override val signIn: String = context.getString(R.string.sign_in)
    }
}
