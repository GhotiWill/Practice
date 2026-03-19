package com.example.bardakovexam.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bardakovexam.presentation.navigation.navRoutes
import com.example.bardakovexam.presentation.viewModels.FavoriteViewModel

@Composable
fun FavoriteScreen(navController: NavController, viewModel: FavoriteViewModel = hiltViewModel()) {
    Box(modifier = Modifier.fillMaxSize().background(AppBackground)) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 18.dp)) {
            Text("Избранное", color = AppText, fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(24.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(viewModel.products.value) { product ->
                    ProductCard(
                        product = product,
                        isFavorite = viewModel.favoriteIds.value.contains(product.id),
                        onFavoriteToggle = { viewModel.toggleFavorite(product.id) }
                    )
                }
            }
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) { BottomNavBar(navRoutes.favorite, navController) }
    }
}
