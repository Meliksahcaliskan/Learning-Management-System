package com.example.loginmultiplatform.ui

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.google.android.material.datepicker.MaterialDatePicker
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.*
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import com.example.loginmultiplatform.R
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*
import com.example.loginmultiplatform.model.AttendanceResponse
import com.example.loginmultiplatform.model.AttendanceStats
import com.example.loginmultiplatform.model.StudentCourseResponse
import com.example.loginmultiplatform.viewmodel.AttendanceViewModel
import com.example.loginmultiplatform.utils.CreateAttendancePDF

val customFontFamily = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_bold, FontWeight.Bold),
    Font(R.font.montserrat_semibold, FontWeight.Bold)
)

fun formatToReadableDate(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = inputFormat.parse(dateString)

    val outputFormat = SimpleDateFormat("d MMMM\nEEEE", Locale("tr"))
    return outputFormat.format(date)
}

@Composable
actual fun AttendanceScreen(viewModel: AttendanceViewModel, navController: NavController, studentId: Int, classId: Int) {

    val attendanceList by viewModel.attendanceList.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val loading by viewModel.isLoading.collectAsState()
    val attendanceStats by viewModel.attendanceStats.collectAsState()
    val groupedData = attendanceList.groupBy { it.courseId } //derslere göre gruplandırma
    val coursesList by viewModel.studentCourses.collectAsState()
    val calendar = Calendar.getInstance()



    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    val defaultStartDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)) }
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek + 6)
    val defaultEndDate by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)) }
    var showDatePicker by remember { mutableStateOf(false) }
    var resetVisible by remember { mutableStateOf(false) }

    var startDate by remember { mutableStateOf(defaultStartDate) }
    var endDate by remember { mutableStateOf(defaultEndDate) }

    LaunchedEffect(studentId, classId) {
        try {
            viewModel.fetchAttendanceStats(studentId, classId)
            viewModel.fetchStudentCourses(studentId)
            viewModel.fetchAttendance(studentId, startDate, endDate)
        } catch (e: Exception) {
            Log.e("AttendanceScreen", "Error fetching data: ${e.message}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "Bir hata oluştu",
                color = MaterialTheme.colors.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            val context = LocalContext.current

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                item { Legend(context, groupedData, startDate, endDate, statistics = attendanceStats, courses = coursesList, classId = classId) }

                item {
                    Button(
                        onClick = { showDatePicker = true},
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF5270FF))
                    ) {
                        Text("Tarih Aralığı Seç", fontFamily = customFontFamily, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        SelectedDateRangeDisplay(startDate = startDate, endDate = endDate)

                        if (resetVisible) {
                            IconButton(
                                onClick = {
                                    startDate = defaultStartDate
                                    endDate = defaultEndDate
                                    viewModel.fetchAttendance(studentId, startDate, endDate)
                                    resetVisible = false
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Tarihleri sıfırla",
                                    tint = Color.Red
                                )
                            }
                        }

                        //Spacer(modifier = Modifier.width(4.dp))
                        //println(resetVisible)

                    }
                }

                if (showDatePicker) {
                    item {
                        DateRangePicker(
                            initialStartDate = startDate,
                            initialEndDate = endDate,
                            onDateRangeSelected = { selectedStartDate, selectedEndDate ->
                                startDate = selectedStartDate
                                endDate = selectedEndDate
                                viewModel.fetchAttendance(studentId, startDate, endDate)
                                showDatePicker = false
                                resetVisible = true
                            },
                            onDismiss = { showDatePicker = false }

                        )
                    }
                }

                groupedData.forEach { (courseId, attendances) ->
                    val course = coursesList.find { it.id.toLong() == courseId }
                    if (course != null) {
                        val sortedAttendances = attendances.sortedByDescending { it.date }
                        val filteredAttendances = sortedAttendances.filter { it.status != "PRESENT" }

                        if (filteredAttendances.isNotEmpty()) {
                            item {
                                ExpendableTableCard(
                                    lessonName = course.name,
                                    attendances = filteredAttendances,
                                    statistics = attendanceStats,
                                    classId = classId,
                                    courseId = courseId.toInt()
                                )
                            }
                        } else {
                            item {
                                Text(
                                    text = "Yoklama Kaydı Bulunamadı",
                                    style = MaterialTheme.typography.h6,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    fontFamily = customFontFamily,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                /*item {
                    Button(
                        onClick = {
                            CreateAttendancePDF(context = context, groupedData = groupedData, startDate = startDate, endDate = endDate, studentName = "Alice")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF5270FF))
                    ) {
                        Text(
                            text = "İndir",
                            fontFamily = customFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }*/
            }
        }
    }
}

@Composable
fun SelectedDateRangeDisplay(startDate: String, endDate: String) {

    // Başlangıç Tarihi
    Box(
        modifier = Modifier
            .background(
                color = Color(0xFF5270FF),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = startDate,
            style = MaterialTheme.typography.body2.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = customFontFamily
            )
        )
    }

    Spacer(modifier = Modifier.width(8.dp))

    Text(
        text = "-",
        style = MaterialTheme.typography.body1.copy(
            color = MaterialTheme.colors.onSurface,
            fontWeight = FontWeight.Bold,
            fontFamily = customFontFamily
        )
    )

    Spacer(modifier = Modifier.width(8.dp))

    // Bitiş Tarihi
    Box(
        modifier = Modifier
            .background(
                color = Color(0xFF5270FF),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = endDate,
            style = MaterialTheme.typography.body2.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = customFontFamily
            )
        )
    }
}


@Composable
fun DateRangePicker(
    initialStartDate: String,
    initialEndDate: String,
    onDateRangeSelected: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val initialRange = androidx.core.util.Pair(
        dateFormat.parse(initialStartDate)?.time,
        dateFormat.parse(initialEndDate)?.time
    )
    val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
        .setTitleText("Tarih Aralığı Seç")
        .setSelection(initialRange)
        .build()

    dateRangePicker.addOnPositiveButtonClickListener { range ->
        val startMillis = range.first
        val endMillis = range.second

        if (startMillis != null && endMillis != null) {
            val selectedStartDate = dateFormat.format(Date(startMillis))
            val selectedEndDate = dateFormat.format(Date(endMillis))
            onDateRangeSelected(selectedStartDate, selectedEndDate)
        }
    }

    dateRangePicker.addOnDismissListener {
        onDismiss()
    }

    dateRangePicker.show((context as AppCompatActivity).supportFragmentManager, "date_range_picker")
}

@Composable
fun ExpendableTableCard(
    lessonName: String,
    attendances: List<AttendanceResponse>,
    statistics: List<AttendanceStats>,
    classId: Int,
    courseId: Int
) {

    var expanded by remember { mutableStateOf(false) }
    val filteredStats = statistics.filter { it.classId == classId && it.courseId == courseId}

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

            Divider()

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TableHeaderCell("TARİH", Modifier.weight(2f))
                        TableHeaderCell("SAAT", Modifier.weight(1f))
                        TableHeaderCell("AÇIKLAMA", Modifier.weight(3f))
                    }

                    Divider(color = Color(0xFF5A5A5A), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(4.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(attendances) { item ->
                            DataRow(item)
                        }
                    }

                    Divider(color = Color(0xFF5A5A5A), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

                    filteredStats.forEach { stat ->
                        AttendanceStatsArea(statistics = stat)
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceStatsArea(statistics: AttendanceStats) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        StatisticsRow(label = "Toplam Ders Sayısı: ", value = statistics.totalClasses.toString())
        StatisticsRow(label = "Devam Oranı: ", value = "${statistics.attendancePercentage}%")
        StatisticsRow(label = "Katıldığı Dersler: ", value = statistics.presentCount.toString())
        StatisticsRow(label = "Gelmediği Dersler: ", value = statistics.absentCount.toString())
        StatisticsRow(label = "Geç Kaldığı Dersler: ", value = statistics.lateCount.toString())
    }
}

@Composable
fun TableHeaderCell(text: String, modifier: Modifier = Modifier) {
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
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(10.dp)
                .offset(x = 10.dp)
                .background(
                    color = when (item.status) {
                        "ABSENT" -> Color.Red
                        "EXCUSED" -> Color(0xFFFFA500)
                        else -> Color.Transparent
                    },
                    shape = CircleShape
                )
        )
        TableCell(formatToReadableDate(item.date), Modifier.weight(2f))
        TableCell("2", Modifier.weight(1f)) // Saat örnek verisi
        TableCell(item.comment ?: "-", Modifier.weight(3f))
    }
}

@Composable
fun TableCell(text: String, modifier: Modifier = Modifier) {

    Text(
        text = text,
        style = MaterialTheme.typography.body2,
        fontFamily = customFontFamily,
        textAlign = TextAlign.Center,
        color = Color.Black,
        modifier = modifier
    )
}

@Composable
fun StatisticsRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Medium, fontFamily = customFontFamily),
            modifier = Modifier.weight(1f),

        )
        Text(
            text = value,
            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold, fontFamily = customFontFamily),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun Legend(context: Context,
           groupedData: Map<Long, List<AttendanceResponse>>,
           startDate: String,
           endDate: String,
           statistics: List<AttendanceStats>,
           courses: List<StudentCourseResponse>,
           classId: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(color = Color.Red, text = "Gelmedi")
        LegendItem(color = Color(0xFFFFA500), text = "Geç Geldi")
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = {
                // İndir butonuna basılınca PDF oluşturma işlemi
                CreateAttendancePDF(context, groupedData, startDate, endDate, statistics, courses, classId)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "İndir",
                tint = Color(0xFF5270FF)
            )
        }
        Text(
            text = "Rapor İndir",
            fontSize = 14.sp,
            color = Color(0xFF5270FF),
            fontFamily = customFontFamily,
            fontWeight = FontWeight.Bold
        )
    }

}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Medium, fontFamily = customFontFamily)
        )
    }
}
