package com.example.loginmultiplatform

import android.content.Context

object AppContext {
    lateinit var context: Context

    fun initialize(context: Context) {
        this.context = context
    }
}