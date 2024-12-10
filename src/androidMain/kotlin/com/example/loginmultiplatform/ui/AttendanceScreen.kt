package com.example.loginmultiplatform.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import com.example.loginmultiplatform.R
import androidx.navigation.NavController
import com.example.loginmultiplatform.model.AttendanceResponse
import com.example.loginmultiplatform.ui.components.BottomNavigationBar
import com.example.loginmultiplatform.ui.components.TopBar
import com.example.loginmultiplatform.viewmodel.AttendanceViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun AttendanceScreen(viewModel: AttendanceViewModel, navController: NavController, studentId: Int, classId: Int) {


    val attendanceList by viewModel.attendanceList.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    //val attendanceStats by viewModel.attendanceStats.collectAsState()
    //val attendanceStatsList = attendanceStats?.data?.let { listOf(it) } ?: emptyList()
    val groupedData = attendanceList.groupBy { it.studentName } //derslere göre gruplandırma
    //val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    //val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(studentId, classId) {
        try {
            //viewModel.fetchAttendanceStats(studentId, classId)
            viewModel.fetchAttendance(studentId, "2024-01-01", "2024-12-31")
        } catch (e: Exception) {
            Log.e("AttendanceScreen", "Error fetching data: ${e.message}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // İçerik burada yer almalı
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "Bir hata oluştu",
                color = MaterialTheme.colors.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                groupedData.forEach { (lessonName, attendances) ->
                    item {
                        ExpendableTableCard(lessonName = lessonName, attendances = attendances)
                    }
                }
            }

            /*Spacer(modifier = Modifier.height(32.dp))
            Divider(color = Color(0XFF5A5A5A), thickness = 1.dp)

            LazyColumn {
                items(attendanceStatsList) { stat ->
                    TableCell("Attendance Percentage: ${stat.attendancePercentage}%")
                    TableCell("Present Count: ${stat.presentCount}")
                    TableCell("Absent Count: ${stat.absentCount}")

                }
            }*/
        }
    }
}

@Composable
fun ExpendableTableCard(lessonName: String, attendances: List<AttendanceResponse>, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded}
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lessonName,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            //Divider(color = Color(0xFF5A5A5A), thickness = 1.dp)

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                        //.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        //TableHeaderCell("Ders Adı", Modifier.weight(1.5f))
                        TableHeaderCell("Tarih", Modifier.weight(2f))
                        TableHeaderCell("Saat", Modifier.weight(1f))
                        TableHeaderCell("Açıklama", Modifier.weight(3f))
                    }

                    Divider(color = Color(0xFF5A5A5A), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(4.dp))
                }


                // Veriler doğru şekilde hizalanıyor
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .padding(top = 48.dp), // Divider ile LazyColumn arasında bir hizalama sağlıyor
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(attendances) { item ->
                        DataRow(item)
                    }
                }
            }
        }
    }
}

@Composable
fun TableHeaderCell(text: String, modifier: Modifier = Modifier) {
    val customFontFamily = FontFamily(
        Font(R.font.montserrat_regular, FontWeight.Normal),
        Font(R.font.montserrat_bold, FontWeight.Bold),
        Font(R.font.montserrat_semibold, FontWeight.Bold)
    )

    Text(
        text = text,
        style = MaterialTheme.typography.body2.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center,
        fontFamily = customFontFamily,
        color = Color.Black,
        modifier = modifier
    )
}

@Composable
fun DataRow(item: AttendanceResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        //TableCell(item.studentName, Modifier.weight(1.5f))
        TableCell(item.date, Modifier.weight(2f))
        TableCell("2", Modifier.weight(1f)) // Saat örnek verisi
        TableCell(item.comment ?: "-", Modifier.weight(3f))
    }
}

@Composable
fun TableCell(text: String, modifier: Modifier = Modifier) {
    val customFontFamily = FontFamily(
        Font(R.font.montserrat_regular, FontWeight.Normal),
        Font(R.font.montserrat_bold, FontWeight.Bold),
        Font(R.font.montserrat_semibold, FontWeight.Bold)
    )

    Text(
        text = text,
        style = MaterialTheme.typography.body2,
        fontFamily = customFontFamily,
        textAlign = TextAlign.Center,
        color = Color.Black,
        modifier = modifier
    )
}