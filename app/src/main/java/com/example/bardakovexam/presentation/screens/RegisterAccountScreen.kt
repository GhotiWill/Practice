package com.example.bardakovexam.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bardakovexam.domain.states.AppState
import com.example.bardakovexam.presentation.navigation.navRoutes
import com.example.bardakovexam.presentation.viewModels.RegisterAccountViewModel

@Composable
fun RegisterAccountScreen(navController: NavController, viewModel: RegisterAccountViewModel = hiltViewModel()) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var accepted by remember { mutableStateOf(true) }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is AppState.Success) navController.navigate(navRoutes.signIn)
    }

    ScreenContainer(Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        BackCircleButton { navController.navigate(navRoutes.signIn) }
        Spacer(modifier = Modifier.height(24.dp))
        HeaderTitle(title = "Регистрация", subtitle = "Заполните Свои Данные")
        Spacer(modifier = Modifier.height(52.dp))
        AuthField(label = "Ваше имя", value = name, onValueChange = { name = it })
        Spacer(modifier = Modifier.height(18.dp))
        AuthField(label = "Email", value = email, onValueChange = { email = it })
        Spacer(modifier = Modifier.height(18.dp))
        AuthField(
            label = "Пароль",
            value = password,
            onValueChange = { password = it },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailing = { TextButton(onClick = { showPassword = !showPassword }) { Text(if (showPassword) "🙈" else "👁", color = AppMuted) } }
        )
        Spacer(modifier = Modifier.height(20.dp))
        PrimaryButton(text = "Зарегистрироваться", onClick = {
            viewModel.clearError()
            viewModel.register(name, email, password)
        })
        Spacer(modifier = Modifier.weight(1f))
        androidx.compose.foundation.layout.Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("Есть аккаунт? ", color = AppMuted, fontSize = 16.sp)
            TextButton(onClick = { navController.navigate(navRoutes.signIn) }) { Text("Войти", color = AppText, fontSize = 16.sp) }
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
