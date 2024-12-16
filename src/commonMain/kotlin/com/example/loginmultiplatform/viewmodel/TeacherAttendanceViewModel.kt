package com.example.loginmultiplatform.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginmultiplatform.model.TeacherClassResponse
import com.example.loginmultiplatform.repository.TeacherAttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeacherAttendanceViewModel : ViewModel() {

    private val repository = TeacherAttendanceRepository()

    private val _teacherClasses = MutableStateFlow<List<TeacherClassResponse>>(emptyList())
    val teacherClasses: StateFlow<List<TeacherClassResponse>> = _teacherClasses

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchTeacherClasses()
    }

    fun fetchTeacherClasses() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val classes = repository.getTeacherClasses()
                if (classes.success) {
                    _teacherClasses.value = classes.data
                } else {
                    _errorMessage.value = classes.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Bir hata oluştu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
