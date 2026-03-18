package com.example.bardakovexam.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bardakovexam.domain.states.AppState
import com.example.bardakovexam.presentation.navigation.navRoutes
import com.example.bardakovexam.presentation.viewModels.ForgotPasswordViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun ForgotPasswordScreen(navController: NavController, viewModel: ForgotPasswordViewModel = hiltViewModel()) {
    var email by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()

    ScreenContainer(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        BackCircleButton { navController.navigate(navRoutes.signIn) }
        Spacer(modifier = Modifier.height(30.dp))
        HeaderTitle(title = "Забыл Пароль", subtitle = "Введите Свою Учетную Запись\nДля Сброса")
        Spacer(modifier = Modifier.height(48.dp))
        AuthField(label = "", value = email, onValueChange = { email = it })
        Spacer(modifier = Modifier.height(32.dp))
        PrimaryButton(text = "Отправить", onClick = {
            viewModel.email = email
            viewModel.sendCode()
        })
    }

    if (state is AppState.Success) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(onClick = { navController.navigate("${navRoutes.verificationBase}/$email") }) { Text("OK", color = AppBlue) }
            },
            text = {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
                    androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.background(AppBlue, CircleShape).padding(18.dp)) { Text("✉", color = Color.White, fontSize = 22.sp) }
                        Text("Проверьте Ваш Email", color = AppText, fontSize = 18.sp, modifier = Modifier.padding(top = 20.dp))
                        Text(
                            "Мы Отправили Код Восстановления\nПароля На Вашу Электронную Почту.",
                            color = AppMuted,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = Color.White
        )
    }
}
