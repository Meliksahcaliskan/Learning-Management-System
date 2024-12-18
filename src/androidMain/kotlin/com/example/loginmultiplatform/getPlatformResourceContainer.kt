package com.example.loginmultiplatform

//import com.example.loginmultiplatform.ResourceContainer
import android.content.Context

actual fun getPlatformResourceContainer(): ResourceContainer {
    val context = AppContext.context

    return object : ResourceContainer {
        override val logo: Int = R.drawable.logo
        override val notification: Int = R.drawable.notification
        override val pp: Int = R.drawable.pp
        override val sidemenu: Int = R.drawable.sidemenu
        override val mainpage: Int = R.drawable.mainpage
        override val exams: Int = R.drawable.exams
        override val homework: Int = R.drawable.homework
        override val attendance: Int = R.drawable.attendance
        override val announcement: Int = R.drawable.announcement
        override val eyeOpen: Int = R.drawable.ic_eye_open
        override val eyeClose: Int = R.drawable.ic_eye_close
        override val lighthouse: Int = R.drawable.lighthouse
        override val appLogo: Int = R.drawable.app_logo
        override val settings: Int = R.drawable.settings
        override val logo_sekil: Int = R.drawable.logo_sekil
        override val logo_yazi: Int = R.drawable.logo_yazi
        override val app_logo_login: Int = R.drawable.app_logo_login
        override val welcomeAgain: String = context.getString(R.string.welcome_again)
        override val emailPlaceholder: String = context.getString(R.string.email_placeholder)
        override val passwordPlaceholder: String = context.getString(R.string.password_placeholder)
        override val rememberMe: String = context.getString(R.string.remember_me)
        override val forgotPassword: String = context.getString(R.string.forgot_password)
        override val signIn: String = context.getString(R.string.sign_in)
    }
}
