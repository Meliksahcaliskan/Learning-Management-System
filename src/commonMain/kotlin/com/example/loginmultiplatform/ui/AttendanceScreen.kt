package com.example.loginmultiplatform.ui

import androidx.compose.runtime.Composable
import com.example.loginmultiplatform.viewmodel.AttendanceViewModel
import androidx.navigation.NavController

@Composable
expect fun AttendanceScreen(viewModel: AttendanceViewModel = AttendanceViewModel() ,navController: NavController, studentId: Int, classId: Int)
