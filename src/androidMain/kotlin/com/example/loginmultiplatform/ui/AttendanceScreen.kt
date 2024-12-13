package com.example.loginmultiplatform.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
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
import androidx.compose.ui.text.font.FontStyle
import com.example.loginmultiplatform.R
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*
import com.example.loginmultiplatform.model.AttendanceResponse
import com.example.loginmultiplatform.model.AttendanceStats
import com.example.loginmultiplatform.ui.components.BottomNavigationBar
import com.example.loginmultiplatform.ui.components.TopBar
import com.example.loginmultiplatform.viewmodel.AttendanceViewModel

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
    //val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    //val coroutineScope = rememberCoroutineScope()
    val coursesList by viewModel.studentCourses.collectAsState()

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val defaultStartDate by remember { mutableStateOf("$currentYear-01-01") }
    val defaultEndDate by remember { mutableStateOf("$currentYear-12-31") }
    var showDatePicker by remember { mutableStateOf(false) }
    var resetVisible by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf(defaultStartDate) }
    var endDate by remember { mutableStateOf(defaultEndDate) }


    fun isValidDate(date: String): Boolean {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formatter.isLenient = false
            formatter.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

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
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                item { Legend() }

                item {
                    Button(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF5270FF))
                    ) {
                        Text("Tarih Aralığı Seç", fontFamily = customFontFamily, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    if (resetVisible) {
                        Button(
                            onClick = {
                                startDate = defaultStartDate
                                endDate = defaultEndDate
                                viewModel.fetchAttendance(studentId, startDate, endDate)
                                resetVisible = false
                            },
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF5270FF))
                        ) {
                            Text("Sıfırla", fontFamily = customFontFamily, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }


                    SelectedDateRangeDisplay(startDate = startDate, endDate = endDate)
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
                                    defaultStartDate = startDate,
                                    defaultEndDate = endDate,
                                    onFilter = { startDate, endDate ->
                                        viewModel.fetchAttendance(studentId, startDate, endDate)
                                    },
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
fun SelectedDateRangeDisplay(startDate: String, endDate: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
    defaultStartDate: String,
    defaultEndDate: String,
    onFilter: (String, String) -> Unit,
    statistics: List<AttendanceStats>,
    classId: Int,
    courseId: Int
) {

    var expanded by remember { mutableStateOf(false) }
    val filteredStats = statistics.filter { it.classId == classId && it.courseId == courseId}
    /*var startDate by remember { mutableStateOf(defaultStartDate) }
    var endDate by remember { mutableStateOf(defaultEndDate) }
    val datePattern = Regex("\\d{4}-\\d{2}-\\d{2}")*/


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

                    /*Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startDate,
                            onValueChange = { value ->
                                startDate = value
                                if (datePattern.matches(value) && datePattern.matches(endDate)) {
                                    onFilter(startDate, endDate)
                                }
                            },
                            label = { Text("Başlangıç Tarihi (YYYY-AA-GG)") },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        OutlinedTextField(
                            value = endDate,
                            onValueChange = { value ->
                                endDate = value
                                if (datePattern.matches(value) && datePattern.matches(endDate)) {
                                    onFilter(startDate, endDate)
                                }
                            },
                            label = { Text("Bitiş Tarihi (YYYY-AA-GG)") },
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }*/

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        //TableHeaderCell("Ders Adı", Modifier.weight(1.5f))
                        TableHeaderCell("TARİH", Modifier.weight(2f))
                        TableHeaderCell("SAAT", Modifier.weight(1f))
                        TableHeaderCell("AÇIKLAMA", Modifier.weight(3f))
                    }

                    Divider(color = Color(0xFF5A5A5A), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(4.dp))

                    // Veriler doğru şekilde hizalanıyor
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                            //.padding(top = 48.dp), // Divider ile LazyColumn arasında bir hizalama sağlıyor
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

        //TableCell(item.studentName, Modifier.weight(1.5f))
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
fun Legend() {
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
