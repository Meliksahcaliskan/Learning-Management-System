// File: src/androidMain/kotlin/com/example/loginmultiplatform/MainActivity.kt

package com.example.loginmultiplatform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.loginmultiplatform.ui.LoginScreen
import com.example.loginmultiplatform.AppContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize AppContext if using the singleton approach
        AppContext.context = this.applicationContext

        setContent {
            // Call your LoginScreen composable
            LoginScreen()
        }
    }
}
