package com.example.loginmultiplatform.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

    fun saveLoginDetails(username: String, password: String) {
        prefs.edit()
            .putString("username", username)
            .putString("password", password)
            .apply()
    }

    fun getLoginDetails(): Pair<String, String> {
        val username = prefs.getString("username", "") ?: ""
        val password = prefs.getString("password", "") ?: ""
        return Pair(username, password)
    }

    fun clearLoginDetails() {
        prefs.edit()
            .remove("username")
            .remove("password")
            .apply()
    }
}