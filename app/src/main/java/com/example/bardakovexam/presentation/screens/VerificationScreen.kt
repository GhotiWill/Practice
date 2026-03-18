package com.example.bardakovexam.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bardakovexam.domain.states.AppState
import com.example.bardakovexam.presentation.navigation.navRoutes
import com.example.bardakovexam.presentation.viewModels.VerificationViewModel

@Composable
fun VerificationScreen(navController: NavController, email: String, viewModel: VerificationViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val seconds by viewModel.seconds.collectAsState()
    var code by remember { mutableStateOf("") }

    LaunchedEffect(state) { if (state is AppState.Success) navController.navigate(navRoutes.newPassword) }

    ScreenContainer(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        BackCircleButton { navController.navigate(navRoutes.forgotPassword) }
        Spacer(modifier = Modifier.height(28.dp))
        HeaderTitle(title = "OTP Проверка", subtitle = "Пожалуйста, Проверьте Свою\nЭлектронную Почту, Чтобы Увидеть Код\nПодтверждения")
        Spacer(modifier = Modifier.height(28.dp))
        Text("OTP Код", color = AppText, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(18.dp))
        AuthField(label = "", value = code, onValueChange = { if (it.length <= 8) { code = it; if (it.length == 8) viewModel.verify(email, it) } })
        Spacer(modifier = Modifier.height(16.dp))
        if (seconds > 0) {
            Text("00:${seconds.toString().padStart(2, '0')}", color = AppMuted, fontSize = 15.sp, modifier = Modifier.fillMaxWidth())
        } else {
            TextButton(onClick = { viewModel.startTimer() }) { Text("Отправить заново", color = AppMuted, fontSize = 15.sp) }
        }
        Spacer(modifier = Modifier.height(18.dp))

    }
}
