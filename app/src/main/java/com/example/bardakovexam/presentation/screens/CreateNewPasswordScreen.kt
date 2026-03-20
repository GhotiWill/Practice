package com.example.bardakovexam.presentation.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bardakovexam.domain.states.AppState
import com.example.bardakovexam.presentation.navigation.navRoutes
import com.example.bardakovexam.presentation.viewModels.CreateNewPasswordViewModel

@Composable
fun CreateNewPasswordScreen(navController: NavController, viewModel: CreateNewPasswordViewModel = hiltViewModel()) {
    var password by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showRepeat by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) { if (state is AppState.Success) navController.navigate(navRoutes.signIn) }

    ScreenContainer(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        BackCircleButton { navController.navigateUp() }
        Spacer(modifier = Modifier.height(28.dp))
        HeaderTitle(title = "Задать Новый Пароль", subtitle = "Установите Новый Пароль Для Входа В\nВашу Учетную Запись")
        Spacer(modifier = Modifier.height(48.dp))
        AuthField(
            label = "Пароль",
            value = password,
            onValueChange = { password = it },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailing = { PasswordVisibilityIcon(isVisible = showPassword, onClick = { showPassword = !showPassword }) }
        )
        Spacer(modifier = Modifier.height(22.dp))
        AuthField(
            label = "Подтверждение пароля",
            value = repeat,
            onValueChange = { repeat = it },
            visualTransformation = if (showRepeat) VisualTransformation.None else PasswordVisualTransformation(),
            trailing = { TextButton(onClick = { showRepeat = !showRepeat }) { Text(if (showRepeat) "🙈" else "👁", color = AppMuted) } }
        )
        Spacer(modifier = Modifier.height(38.dp))
        PrimaryButton(text = "Сохранить", onClick = { viewModel.save(password, repeat) })
    }

    if (state is AppState.Error) {
        AlertDialog(onDismissRequest = {}, confirmButton = { TextButton({}) { Text("OK") } }, title = { Text("Ошибка") }, text = { Text((state as AppState.Error).ex.message.orEmpty()) })
    }
}
