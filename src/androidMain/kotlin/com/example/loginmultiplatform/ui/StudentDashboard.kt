package com.example.loginmultiplatform.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.loginmultiplatform.viewmodel.LoginViewModel
import com.example.loginmultiplatform.getPlatformResourceContainer

@Composable
actual fun StudentDashboard(navController: NavController, viewModel: LoginViewModel) {
    val studentId by viewModel.studentId.collectAsState(initial = null)
    //println("studentId: $studentId")
    val resources = getPlatformResourceContainer()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Navigation Bar
        Row(
            modifier = Modifier
                .background(color = Color(0xFF5D5FEF))
                .fillMaxWidth()
                .height(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = resources.logo),
                modifier = Modifier
                    .padding(start = 10.dp, top = 5.dp, bottom = 5.dp)
                    .size(60.dp),
                contentDescription = "Logo"
            )

            IconButton(
                modifier = Modifier
                    .padding(start = 40.dp, top = 35.dp)
                    .background(color = Color.White, shape = CircleShape)
                    .size(40.dp),
                onClick = { println("Clicked_notification") }
            ) {
                Icon(
                    painter = painterResource(id = resources.notification),
                    modifier = Modifier.size(20.dp),
                    contentDescription = "notification"
                )
            }

            Image(
                painter = painterResource(id = resources.pp),
                modifier = Modifier
                    .padding(start = 10.dp, top = 7.dp)
                    .size(65.dp)
                    .clip(CircleShape),
                contentDescription = "Profile Picture"
            )

            Text(
                text = "Hog Rider",
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 5.dp),
                color = Color.White
            )

            IconButton(
                modifier = Modifier
                    .size(70.dp)
                    .padding(start = 5.dp),
                onClick = { println("Opened: Side Bar") }
            ) {
                Icon(
                    painter = painterResource(id = resources.sidemenu),
                    modifier = Modifier.size(25.dp),
                    contentDescription = "Side Menu icon"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Welcome to the Student Dashboard")
                Button(onClick = {
                    navController.navigate("attendance_screen/$studentId/1") // Replace 456 with the correct classId
                }) {
                    Text(text = "Go to Attendance Screen")
                }
            }
        }

        // Bottom Navigation Bar
        Row(
            modifier = Modifier
                .background(color = Color(0xFF5D5FEF))
                .fillMaxWidth()
                .height(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .height(58.dp)
                    .width(70.dp)
                    .padding(start = 15.dp)
                    .background(color = Color.White),
                onClick = { println("Opened: Main_page") }
            ) {
                Icon(
                    painter = painterResource(id = resources.mainpage),
                    modifier = Modifier.size(20.dp),
                    contentDescription = "Main Page icon"
                )
            }

            IconButton(
                modifier = Modifier
                    .height(58.dp)
                    .width(70.dp)
                    .padding(start = 15.dp)
                    .background(color = Color.White),
                onClick = { println("Opened: Exams_page") }
            ) {
                Icon(
                    painter = painterResource(id = resources.exams),
                    modifier = Modifier.size(20.dp),
                    contentDescription = "Exams icon"
                )
            }

            IconButton(
                modifier = Modifier
                    .height(58.dp)
                    .width(70.dp)
                    .padding(start = 15.dp)
                    .background(color = Color.White),
                onClick = { println("Opened: homework_page") }
            ) {
                Icon(
                    painter = painterResource(id = resources.homework),
                    modifier = Modifier.size(20.dp),
                    contentDescription = "Homework icon"
                )
            }

            IconButton(
                modifier = Modifier
                    .height(58.dp)
                    .width(70.dp)
                    .padding(start = 15.dp)
                    .background(color = Color.White),
                onClick = {
                    println("Opened: attendance_page")
                    navController.navigate("attendance_screen/$studentId/1") // Replace 456 with the correct classId
                }
            ) {
                Icon(
                    painter = painterResource(id = resources.attendance),
                    modifier = Modifier.size(20.dp),
                    contentDescription = "Attendance icon"
                )
            }

            IconButton(
                modifier = Modifier
                    .height(58.dp)
                    .width(70.dp)
                    .padding(start = 15.dp)
                    .background(color = Color.White),
                onClick = { println("Opened: announcement_page") }
            ) {
                Icon(
                    painter = painterResource(id = resources.announcement),
                    modifier = Modifier.size(20.dp),
                    contentDescription = "Announcement icon"
                )
            }
        }
    }
}
