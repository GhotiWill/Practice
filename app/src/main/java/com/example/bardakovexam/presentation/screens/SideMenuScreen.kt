package com.example.bardakovexam.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bardakovexam.data.remotes.SessionManager
import com.example.bardakovexam.domain.states.AppState
import com.example.bardakovexam.presentation.navigation.navRoutes
import com.example.bardakovexam.presentation.viewModels.SideMenuViewModel

@Composable
fun SideMenuScreen(navController: NavController, viewModel: SideMenuViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state) { if (state is AppState.Success) navController.navigate(navRoutes.signIn) }
    val items = listOf("Профиль", "Корзина", "Избранное", "Заказы", "Уведомления", "Настройки")
    Column(modifier = Modifier.fillMaxSize().background(AppBlue).padding(horizontal = 28.dp, vertical = 44.dp)) {
        Box(modifier = Modifier.size(92.dp).background(Color.White.copy(alpha = 0.25f), CircleShape), contentAlignment = Alignment.Center) {
            Text((SessionManager.email ?: "E").take(1), color = Color.White, fontSize = 34.sp)
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(SessionManager.email ?: "Пользователь", color = Color.White, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(42.dp))
        items.forEach { item ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(item, color = Color.White, fontSize = 18.sp)
                Text("›", color = Color.White, fontSize = 24.sp)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.45f)))
        Spacer(modifier = Modifier.height(24.dp))
        Text("Выйти", color = Color.White, fontSize = 20.sp, modifier = Modifier.background(Color.Transparent).padding(vertical = 12.dp).align(Alignment.Start))
        PrimaryButton(text = "Выйти", onClick = { viewModel.logout() }, modifier = Modifier.padding(top = 12.dp))
    }
}
