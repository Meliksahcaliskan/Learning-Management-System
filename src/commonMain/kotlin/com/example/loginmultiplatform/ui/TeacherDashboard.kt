package com.example.loginmultiplatform.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.loginmultiplatform.viewmodel.LoginViewModel
import com.example.loginmultiplatform.viewmodel.TeacherAttendanceViewModel

@Composable
expect fun TeacherDashboard(loginViewModel: LoginViewModel = LoginViewModel(), teacherAttendanceViewModel: TeacherAttendanceViewModel = TeacherAttendanceViewModel(), navController: NavController)