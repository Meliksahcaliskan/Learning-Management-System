package com.example.loginmultiplatform.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.loginmultiplatform.getPlatformResourceContainer
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.loginmultiplatform.R
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch

@Composable
actual fun TopBar(userName: String?, onSettingsClick: () -> Unit, onProfileClick: () -> Unit) {

    val customFontFamily = FontFamily(
        Font(R.font.montserrat_regular, FontWeight.Normal),
        Font(R.font.montserrat_bold, FontWeight.Bold),
        Font(R.font.montserrat_semibold, FontWeight.Bold)
    )

    Row(
        modifier = Modifier
            .background(color = Color(0xFF5270FF))
            .fillMaxWidth()
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        /*// Logo
        Image(
            painter = painterResource(id = getPlatformResourceContainer().logo),
            modifier = Modifier
                .padding(start = 10.dp, top = 5.dp, bottom = 5.dp)
                .size(60.dp),
            contentDescription = "Logo"
        )*/

        IconButton(
            modifier = Modifier.padding(start = 8.dp),
            onClick = onSettingsClick
        ) {
            Icon(
                painter = painterResource(id = getPlatformResourceContainer().settings),
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Image(
            painter = painterResource(id = getPlatformResourceContainer().appLogo),
            modifier = Modifier.size(100.dp),
            contentDescription = "App Logo"
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(
                text = userName ?: "",
                fontSize = 14.sp,
                color = Color.White,
                fontFamily = customFontFamily,
                modifier = Modifier.padding(end = 8.dp)
            )

            IconButton(
                onClick = onProfileClick
            ) {
                Image(
                    painter = painterResource(id = getPlatformResourceContainer().pp),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentDescription = "Profile Picture"
                )
            }
        }

        /*// Notification Button
        IconButton(
            modifier = Modifier
                .padding(start = 40.dp, top = 35.dp)
                .background(color = Color.White, shape = CircleShape)
                .size(40.dp),
            onClick = onNotificationClick
        ) {
            Icon(
                painter = painterResource(id = getPlatformResourceContainer().notification),
                modifier = Modifier.size(20.dp),
                contentDescription = "Notification"
            )
        }

        // Profile Picture
        Image(
            painter = painterResource(id = getPlatformResourceContainer().pp),
            modifier = Modifier
                .padding(start = 10.dp, top = 7.dp)
                .size(65.dp)
                .clip(CircleShape),
            contentDescription = "Profile Picture"
        )

        // User Name
        Text(
            text = "Hog Rider",
            fontSize = 13.sp,
            modifier = Modifier.padding(start = 5.dp),
            color = Color.White,
            fontFamily = customFontFamily
        )

        // Side Menu Button
        IconButton(
            modifier = Modifier
                .size(70.dp)
                .padding(start = 5.dp),
            onClick = onMenuClick
        ) {
            Icon(
                painter = painterResource(id = getPlatformResourceContainer().sidemenu),
                modifier = Modifier.size(25.dp),
                contentDescription = "Side Menu icon"
            )
        }*/
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun BottomNavigationBar(pagerState: PagerState) {
    val items = listOf("Yoklama","Ana Menü","Ödev")
    val coroutineScope = rememberCoroutineScope()

    val customFontFamily = FontFamily(
        Font(R.font.montserrat_regular, FontWeight.Normal),
        Font(R.font.montserrat_bold, FontWeight.Bold),
        Font(R.font.montserrat_semibold, FontWeight.Bold)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFF5270FF))
            .padding(vertical = 12.dp),
            //.shadow(elevation = 8.dp, shape = MaterialTheme.shapes.medium),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, title ->
            val isSelected = pagerState.currentPage == index
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            ) {
                // Metin
                Text(
                    text = title,
                    color = if (isSelected) Color.White else Color.White.copy(0.5f),
                    style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 4.dp),
                    fontFamily = customFontFamily
                )

                // Aktif sayfa göstergesi
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .height(2.dp)
                            .width(16.dp)
                            .background(Color.White, shape = MaterialTheme.shapes.small)
                    )
                }
            }
        }
    }
}

