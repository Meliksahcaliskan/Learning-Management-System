package com.example.loginmultiplatform.ui

import androidx.compose.runtime.Composable
import com.example.loginmultiplatform.viewmodel.LoginViewModel

@Composable
expect fun LoginScreen(viewModel: LoginViewModel = LoginViewModel())