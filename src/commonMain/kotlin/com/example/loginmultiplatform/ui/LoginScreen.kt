package com.example.loginmultiplatform.ui

import androidx.compose.runtime.Composable
import com.example.loginmultiplatform.viewmodel.LoginViewModel
import androidx.navigation.NavController

@Composable
expect fun LoginScreen(viewModel: LoginViewModel = LoginViewModel(),navController: NavController)
