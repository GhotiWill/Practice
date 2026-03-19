package com.example.bardakovexam.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.bardakovexam.R
import com.example.bardakovexam.data.models.Product

@Composable
fun ProductCard(product: Product, isFavorite: Boolean, onFavoriteToggle: () -> Unit) {
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
                        .background(AppField, CircleShape)
                        .clickable(onClick = onFavoriteToggle),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outline),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
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
        }
    }
}
