package com.example.loginmultiplatform.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.loginmultiplatform.model.AttendanceResponse
import com.example.loginmultiplatform.viewmodel.AttendanceViewModel

@Composable
actual fun AttendanceScreen(viewModel: AttendanceViewModel, navController: NavController, studentId: Int, classId: Int) {


    val attendanceList by viewModel.attendanceList.collectAsState()
    val attendanceStats by viewModel.attendanceStats.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val loading by viewModel.isLoading.collectAsState()

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
            //.wrapContentSize(Alignment.Center)
    ) {

        // Tablo Başlıkları
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ders Adı",
                modifier = Modifier.weight(1.5f),
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF5A5A5A),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tarih",
                modifier = Modifier.weight(2f).padding(start = 6.dp),
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF5A5A5A),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Saat",
                modifier = Modifier.weight(1f).padding(start = 16.dp),
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF5A5A5A),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Açıklama",
                modifier = Modifier.weight(3f).padding(start = 16.dp),
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF5A5A5A),
                textAlign = TextAlign.Center
            )
        }

        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
        Spacer(modifier = Modifier.height(2.dp))


        LazyColumn (
            modifier = Modifier
                .fillMaxWidth(),
                //.wrapContentSize(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(attendanceList) { item ->
                DataRow(item)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Toplam Devamsızlık: x gün",
            style = MaterialTheme.typography.body1.copy(color = Color(0XFF5A5A5A)),
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp)
        )
    }
}

@Composable
fun DataRow(item: AttendanceResponse) {
    if (item.status != "PRESENT") {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            elevation = 8.dp,
            /*backgroundColor = if(item.status == "ABSENT") {
                Color(0x30FF0000)
            } else {
                Color(0x30FF7F00)
            },*/

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    //Text(text = "Ders Adı", style = MaterialTheme.typography.caption)
                    Text(
                        text = "Turkce",
                        //modifier = Modifier
                        //.weight(2f)
                        //.padding(start = 8.dp)
                        //.fillMaxWidth(),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Start
                    )
                }

                Column {
                    //Text(text = "Tarih", style = MaterialTheme.typography.caption)
                    Text(
                        text = item.date,
                        //modifier = Modifier
                        //.weight(1.5f)
                        //.fillMaxWidth(),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Start
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    //Text(text = "Saat Sayısı", style = MaterialTheme.typography.caption)
                    Text(
                        text = "2",
                        //modifier = Modifier
                        //.weight(1f)
                        //.fillMaxWidth(),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Start
                    )
                }

                Column {
                    //Text(text = "Açıklama", style = MaterialTheme.typography.caption)
                    item.comment?.let {
                        Text(
                            text = item.comment.ifEmpty { " " },
                            //modifier = Modifier
                            //.weight(2f)
                            //.padding(end = 8.dp)
                            //.fillMaxWidth()
                            style = MaterialTheme.typography.body2,
                            //maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Left
                        )
                    }
                }
            }
        }
    }
}