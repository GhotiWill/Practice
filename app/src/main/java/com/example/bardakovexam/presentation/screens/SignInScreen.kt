package com.example.bardakovexam.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bardakovexam.domain.states.AppState
import com.example.bardakovexam.presentation.navigation.navRoutes
import com.example.bardakovexam.presentation.viewModels.SignInViewModel
import androidx.compose.runtime.collectAsState

@Composable
fun SignInScreen(navController: NavController, viewModel: SignInViewModel = hiltViewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is AppState.Success) navController.navigate(navRoutes.home)
    }

    ScreenContainer(Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        BackCircleButton { navController.navigate(navRoutes.register) }
        Spacer(modifier = Modifier.height(24.dp))
        HeaderTitle(title = "Привет!", subtitle = "Заполните Свои Данные")
        Spacer(modifier = Modifier.height(72.dp))
        AuthField(label = "Email", value = email, onValueChange = { email = it })
        Spacer(modifier = Modifier.height(24.dp))
        AuthField(
            label = "Пароль",
            value = password,
            onValueChange = { password = it },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailing = { TextButton(onClick = { showPassword = !showPassword }) { Text(if (showPassword) "🙈" else "👁", color = AppMuted) } }
        )
        TextButton(onClick = { navController.navigate(navRoutes.forgotPassword) }, modifier = Modifier.align(androidx.compose.ui.Alignment.End)) {
            Text("Восстановить", color = AppMuted, fontSize = 15.sp)
        }
        Spacer(modifier = Modifier.height(14.dp))
        PrimaryButton(text = "Войти", onClick = {
            viewModel.clearError()
            viewModel.signIn(email, password)
        })
        Spacer(modifier = Modifier.weight(1f))
        androidx.compose.foundation.layout.Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("Вы впервые? ", color = AppMuted, fontSize = 16.sp)
            TextButton(onClick = { navController.navigate(navRoutes.register) }) { Text("Создать", color = AppText, fontSize = 16.sp) }
        }
    }

    if (state is AppState.Error) {
        val message = (state as AppState.Error).ex.message ?: "Произошла ошибка"
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            confirmButton = { TextButton(onClick = { viewModel.clearError() }) { Text("OK") } },
            title = { Text("Ошибка") },
            text = { Text(message) }
        )
    }
}
