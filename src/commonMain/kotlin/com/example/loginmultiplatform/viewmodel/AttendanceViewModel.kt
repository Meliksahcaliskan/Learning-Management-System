package com.example.loginmultiplatform.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginmultiplatform.model.AttendanceResponse
import com.example.loginmultiplatform.model.AttendanceStatsResponse
import com.example.loginmultiplatform.model.ResponseWrapper
import com.example.loginmultiplatform.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AttendanceViewModel : ViewModel() {
    private val repository = AttendanceRepository()

    private val _attendanceList = MutableStateFlow<List<AttendanceResponse>>(emptyList())
    val attendanceList: StateFlow<List<AttendanceResponse>> = _attendanceList

    private val _attendanceStats = MutableStateFlow<ResponseWrapper<AttendanceStatsResponse>?>(null)
    val attendanceStats: StateFlow<ResponseWrapper<AttendanceStatsResponse>?> = _attendanceStats

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchAttendance(studentId: Int, startDate: String, endDate: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val attendance = repository.fetchAttendance(studentId, startDate, endDate)
                _attendanceList.value = attendance
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unknown error occured"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /*fun fetchAttendanceStats(studentId: Int, classId: Int) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val stats = repository.fetchAttendanceStats(studentId, classId)
                _attendanceStats.value = stats
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }*/
}