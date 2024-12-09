package com.example.loginmultiplatform.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
expect fun CoordinatorDashboard(navController: NavController, studentId: Int?, classId: Int?)