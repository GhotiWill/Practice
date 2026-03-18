package com.example.bardakovexam.presentation.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bardakovexam.presentation.viewModels.LoyaltyCardViewModel

@Composable
fun LoyaltyCardScreen(navController: NavController, viewModel: LoyaltyCardViewModel = hiltViewModel()) {
    Column(modifier = Modifier.fillMaxSize().background(AppBackground).padding(horizontal = 24.dp, vertical = 18.dp)) {
        androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
            BackCircleButton { navController.navigateUp() }
            Text("Карта лояльности", color = AppText, fontSize = 24.sp, modifier = Modifier.padding(start = 24.dp))
        }
        Spacer(modifier = Modifier.height(48.dp))
        Box(modifier = Modifier.fillMaxWidth().weight(1f).background(Color.White, RoundedCornerShape(24.dp)).padding(24.dp), contentAlignment = Alignment.Center) {
            BarcodeView(viewModel.userId.ifBlank { "12345678901234567890" })
        }
    }
}

@Composable
fun BarcodeView(data: String) {
    Canvas(modifier = Modifier.fillMaxWidth().height(620.dp)) {
        val normalized = if (data.isBlank()) "12345678901234567890" else data
        normalized.forEachIndexed { idx, c ->
            val barWidth = if ((c.code + idx) % 3 == 0) 7f else 4f
            val x = idx * (size.width / (normalized.length + 2))
            val barHeight = if ((c.code + idx) % 5 == 0) size.height * 0.55f else size.height * 0.9f
            drawRect(
                color = Color.Black,
                topLeft = androidx.compose.ui.geometry.Offset(x, (size.height - barHeight) / 2f),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
            )
        }
    }
}
