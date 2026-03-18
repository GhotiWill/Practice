package com.example.bardakovexam.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.bardakovexam.data.models.Product
import com.example.bardakovexam.presentation.viewModels.ProductCardViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductCard(product: Product, viewModel: ProductCardViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .padding(6.dp)
            .size(width = 170.dp, height = 230.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(AppField, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("♥", color = AppDanger, fontSize = 14.sp)
                }
                AsyncImage(
                    model = product.photoUrl(),
                    contentDescription = product.title,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 10.dp)
                        .height(90.dp)
                        .fillMaxWidth()
                )
            }
            Text(
                text = if (product.isBestSeller) "BEST SELLER" else "NEW",
                color = AppBlue,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = product.title,
                color = AppText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 2,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = "₽${"%.2f".format(product.cost)}",
                color = AppText,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(AppBlue, RoundedCornerShape(topStart = 18.dp, bottomEnd = 18.dp))
                        .clickable {
                            scope.launch { viewModel.addFavorite(product.id) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("＋", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Light)
                }
            }
        }
    }
}
