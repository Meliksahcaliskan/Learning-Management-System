// File: src/commonMain/kotlin/com/example/loginmultiplatform/LoginScreen.kt

package com.example.loginmultiplatform.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController
import com.example.loginmultiplatform.R
import com.example.loginmultiplatform.getPlatformResourceContainer
import com.example.loginmultiplatform.ResourceContainer
import com.example.loginmultiplatform.viewmodel.LoginViewModel

@Composable
actual fun LoginScreen(viewModel: LoginViewModel, navController: NavController) {
    //val context = LocalContext.current
    val resources = getPlatformResourceContainer()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") } // Başarı mesajı için state
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") } // Hata mesajı için state

    val customFontFamily = FontFamily(
        Font(R.font.montserrat_regular, FontWeight.Normal),
        Font(R.font.montserrat_bold, FontWeight.Bold),
        Font(R.font.montserrat_semibold, FontWeight.Bold)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Background Image
        Image(
            painter = painterResource(id = resources.lighthouse),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black.copy(0.6f))
        )

        // Foreground Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Icon(
                painter = painterResource(id = resources.appLogo),
                contentDescription = "App Logo",
                modifier = Modifier.size(150.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.height(2.dp))

            // Email Field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(text = resources.emailPlaceholder, color=Color.White.copy(alpha = 0.5f), fontFamily = customFontFamily, fontWeight = FontWeight.Normal) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color(234,228,221)),
                //textFontFamily = customFontFamily,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color(234,228,221)
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            var passwordVisible by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = resources.passwordPlaceholder, color = Color.White.copy(alpha = 0.5f), fontFamily = customFontFamily, fontWeight = FontWeight.Normal) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color(234,228,221)),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {

                    Row {
                        // X icon: Her zaman metin yazıldığında göster
                        if (password.isNotEmpty()) {
                            IconButton(onClick = { password = "" }, modifier = Modifier.alpha(1f)) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                                    contentDescription = "Clear text",
                                    tint = Color.White
                                )
                            }
                        }

                        // Eye icon: Şifre görünürlüğünü kontrol eder
                        IconButton(onClick = { passwordVisible = !passwordVisible }, modifier = Modifier.alpha(1f)) {
                            val eyeIcon = if (passwordVisible) {
                                resources.eyeClose // Şifreyi gösterirken göz
                            } else {
                                resources.eyeOpen // Şifreyi gizlerken göz
                            }
                            Icon(
                                painter = painterResource(id = eyeIcon),
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color(234,228,221)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Remember Me and Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    var rememberMe by remember { mutableStateOf(false) }
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(234,228,221), // Seçili olduğunda beyaz
                            uncheckedColor = Color(234,228,221), // Seçili değilken beyaz
                            checkmarkColor = Color(41,95,152) // İşaret rengi siyah (isteğe bağlı)
                        )

                    )
                    Text(text = resources.rememberMe, color = Color(234,228,221), fontFamily = customFontFamily, fontWeight = FontWeight.Normal)
                }
                TextButton(onClick = { /* Handle forgot password */ }) {
                    Text(text = resources.forgotPassword, color = Color(234,228,221), fontFamily = customFontFamily, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Sign In Button
            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        errorMessage = "Email and Password cannot be empty"
                        showErrorDialog = true
                    } else {
                        viewModel.login(
                            username = username,
                            password = password,
                            onSuccess = { loginData ->
                                successMessage = "Login Successful!"
                                showSuccessDialog = true
                                when (loginData.role) {
                                    "ROLE_STUDENT" -> navController.navigate("student_dashboard")
                                    "ROLE_TEACHER" -> navController.navigate("teacher_dashboard")
                                    "ROLE_COORDINATOR" -> navController.navigate("coordinator_dashboard")
                                    "ROLE_ADMIN" -> navController.navigate("admin_dashboard")
                                }
                            },
                            onError = { error ->
                                errorMessage = "Error: $error"
                                showErrorDialog = true
                            }
                        )
                    }
                },
                modifier = Modifier
                    .width(196.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(234,228,221))
            ) {
                Text(
                    text = resources.signIn,
                    color = Color(41,95,152),
                    fontWeight = FontWeight.Bold,
                    fontFamily = customFontFamily
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (showErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog = false },
                    title = { Text("Error", fontFamily = customFontFamily, fontWeight = FontWeight.Bold, color = Color.Red) },
                    text = { Text(errorMessage, fontFamily = customFontFamily, fontWeight = FontWeight.Normal) },
                    confirmButton = {
                        Button(onClick = { showErrorDialog = false }) {
                            Text("OK", fontFamily = customFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }
    }
}
