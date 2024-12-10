package com.example.loginmultiplatform.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.foundation.pager.PagerState
import androidx.navigation.NavController

@Composable
expect fun TopBar(userName: String?, onSettingsClick: () -> Unit, onProfileClick: () -> Unit)

@OptIn(ExperimentalFoundationApi::class)
@Composable
expect fun BottomNavigationBar(pagerState: androidx.compose.foundation.pager.PagerState)
