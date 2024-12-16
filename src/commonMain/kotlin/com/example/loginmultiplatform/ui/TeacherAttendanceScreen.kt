package com.example.loginmultiplatform.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.loginmultiplatform.viewmodel.TeacherAttendanceViewModel

@Composable
expect fun TeacherAttendanceScreen(viewModel: TeacherAttendanceViewModel = TeacherAttendanceViewModel() ,navController: NavController)