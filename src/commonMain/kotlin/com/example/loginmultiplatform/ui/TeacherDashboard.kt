package com.example.loginmultiplatform.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
expect fun TeacherDashboard(navController: NavController, studentId: Int?, classId: Int?)