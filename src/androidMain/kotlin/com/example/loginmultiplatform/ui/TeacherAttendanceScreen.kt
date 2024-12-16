package com.example.loginmultiplatform.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.loginmultiplatform.R
import com.example.loginmultiplatform.model.TeacherClassResponse
import com.example.loginmultiplatform.viewmodel.TeacherAttendanceViewModel

val timesNewRoman = FontFamily(
    Font(R.font.times, FontWeight.Normal)
)

@Composable
actual fun TeacherAttendanceScreen(viewModel: TeacherAttendanceViewModel, navController: NavController) {

    val classes by viewModel.teacherClasses.collectAsState()

    //öğrenci yoklama durumları için map
    val attendanceOptions = listOf("Katıldı", "Katılmadı", "Geç Geldi")

    //seçili durumlar
    val attendanceStates = remember {
        mutableStateMapOf<Int, String>().apply {
            classes.flatMap { it.studentIdAndNames.entries }.forEach { (studentIdStr, studentName) ->
                val studentId = studentIdStr.toInt()
                this[studentId] = "Katıldı"
            }
        }
    }

    var showStudentDetail by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Spacer(modifier = Modifier.height(16.dp))

        classes.forEach { classt ->
            ExpendableClassCard(
                classInfo = classt,
                attendanceStates = attendanceStates,
                attendanceOptions = attendanceOptions,
                onInfoClick = { studentName ->
                    showStudentDetail = studentName
                }
            )
        }

        //Spacer(modifier = Modifier.height(16.dp))



        //Spacer(modifier = Modifier.height(16.dp))
    }

    if(showStudentDetail != null) {
        StudentAttendanceDetailDialog(
            studentName = showStudentDetail!!,
            onDismiss = { showStudentDetail = null }
        )
    }
}

@Composable
fun ExpendableClassCard(
    classInfo: TeacherClassResponse,
    attendanceStates: MutableMap<Int, String>,
    attendanceOptions: List<String>,
    onInfoClick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(classInfo.name, style = MaterialTheme.typography.body1, fontWeight = FontWeight.Bold, fontFamily = customFontFamily)
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            if (expanded) {
                Divider()

                Column(modifier = Modifier.padding(16.dp)) {
                    classInfo.studentIdAndNames.forEach { (id, student) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { onInfoClick(student)}) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color.Gray, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("i", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = timesNewRoman)
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(student, modifier = Modifier.weight(1f), fontFamily = customFontFamily)

                            var expandedMenu by remember { mutableStateOf(false) }
                            Box {
                                Text(
                                    attendanceStates[id.toInt()] ?: "Katıldı",
                                    modifier = Modifier
                                        .clickable { expandedMenu = true }
                                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontFamily = customFontFamily
                                )
                                DropdownMenu(
                                    expanded = expandedMenu,
                                    onDismissRequest = { expandedMenu = false }
                                ) {
                                    attendanceOptions.forEach { option ->
                                        DropdownMenuItem(onClick = {
                                            attendanceStates[id.toInt()] = option
                                            expandedMenu = false
                                        }) {
                                            Text(option, fontFamily = customFontFamily)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Button(
                    onClick = {
                        //Kaydet api call
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF5270FF))
                ) {
                    Text("Kaydet", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = customFontFamily)
                }

                Divider()
                Spacer(modifier = Modifier.width(16.dp))

                // Kurs İstatistikleri (placeholder)
                Text("Kurs İstatistikleri:", style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold, fontFamily = customFontFamily)
                Text("Toplam Ders: 20", fontFamily = customFontFamily)
                Text("Ortalama Devam: %85", fontFamily = customFontFamily)
                Text("Katıldığı Dersler: 17", fontFamily = customFontFamily)
                Text("Gelmediği Dersler: 3", fontFamily = customFontFamily)
                Text("Geç Kaldığı Dersler: 2", fontFamily = customFontFamily)

                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}

@Composable
fun StudentAttendanceDetailDialog(studentName: String, onDismiss: () -> Unit) {
    // Öğrenci detaylarını gösteren dialog
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column {
                Text("${studentName} - Yoklama Detayları", fontWeight = FontWeight.Bold, fontFamily = customFontFamily)
                Spacer(Modifier.height(8.dp))
                // Burada ilgili öğrenci ve ilgili dersin yoklama geçmişini listeleyin
                // Şimdilik placeholder
                Text("Ders 1: Katıldı\nDers 2: Katılmadı\nDers 3: Geç Geldi", fontFamily = customFontFamily)
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Kapat", fontFamily = customFontFamily)
            }
        }
    )
}
