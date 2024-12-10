package com.example.loginmultiplatform.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.loginmultiplatform.ui.components.BottomNavigationBar
import com.example.loginmultiplatform.ui.components.TopBar
import androidx.navigation.NavController
import com.example.loginmultiplatform.viewmodel.AttendanceViewModel
import com.example.loginmultiplatform.viewmodel.LoginViewModel

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
actual fun StudentDashboard(navController: NavController, loginViewModel: LoginViewModel, attendanceViewModel: AttendanceViewModel) {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })
    val studentId by loginViewModel.studentId.collectAsState()
    val username by loginViewModel.username.collectAsState()

    Scaffold(
        topBar = { TopBar(userName = username, onSettingsClick = {}, onProfileClick = {}) },
        bottomBar = { BottomNavigationBar(pagerState = pagerState) }
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> AttendanceScreen(attendanceViewModel, navController, studentId = studentId ?: -1, classId = 1)
                1 -> DashboardPage("STUDENT DASHBOARD")
                2 -> HomeworkPage("HOMEWORK PAGE")
            }
        }
    }
}

@Composable
fun DashboardPage(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun HomeworkPage(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}
