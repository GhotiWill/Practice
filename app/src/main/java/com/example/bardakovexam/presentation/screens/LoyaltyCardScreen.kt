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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
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
            BarcodeView(viewModel.userId.ifBlank { "12345678901234567890" }, height = 620.dp, horizontalLines = true)
        }
    }
}

@Composable
fun BarcodeView(data: String, height: Dp = 620.dp, horizontalLines: Boolean = false) {
    Canvas(modifier = Modifier.fillMaxWidth().height(height)) {
        val normalized = if (data.isBlank()) "12345678901234567890" else data
        val mainColor = Color.Black
        if (horizontalLines) {
            val step = size.height / (normalized.length + 2)
            val lineWidth = size.width * 0.9f
            normalized.forEachIndexed { idx, c ->
                val thickness = if ((c.code + idx) % 3 == 0) 7f else 4f
                val y = (idx + 1) * step
                drawRect(
                    color = mainColor,
                    topLeft = Offset((size.width - lineWidth) / 2f, y),
                    size = Size(lineWidth, thickness)
                )
            }
        } else {
            val step = size.width / (normalized.length + 2)
            val lineHeight = size.height * 0.9f
            normalized.forEachIndexed { idx, c ->
                val barWidth = if ((c.code + idx) % 3 == 0) 7f else 4f
                val x = (idx + 1) * step
                drawRect(
                    color = mainColor,
                    topLeft = Offset(x, (size.height - lineHeight) / 2f),
                    size = Size(barWidth, lineHeight)
                )
            }
        }
    }
}
