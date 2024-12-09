package com.example.loginmultiplatform.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
expect fun AdminDashboard(navController: NavController, studentId: Int?, classId: Int?)