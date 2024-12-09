package com.example.loginmultiplatform.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
actual fun CoordinatorDashboard(navController: NavController, studentId: Int?, classId: Int?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Welcome to the Coordinator Dashboard")
            Button(onClick = {
                navController.navigate("attendance_screen/$studentId/$classId")
            }) {
                Text(text = "Go to Attendance Screen")
            }
        }
    }
}
