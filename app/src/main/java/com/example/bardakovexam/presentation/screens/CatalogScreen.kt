package com.example.bardakovexam.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.bardakovexam.presentation.viewModels.CatalogViewModel

@Composable
fun CatalogScreen(navController: NavController, viewModel: CatalogViewModel = hiltViewModel()) {
    val selectedCategory = remember { mutableStateOf("Outdoor") }
    Box(modifier = Modifier.fillMaxSize().background(AppBackground)) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 18.dp)) {
            BackCircleButton { navController.navigateUp() }
            Spacer(modifier = Modifier.height(18.dp))
            Text("Outdoor", color = AppText, fontSize = 24.sp, modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(28.dp))
            Text("Категории", color = AppText, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
            CategoryChips(selectedCategory.value, listOf("Все", "Outdoor", "Tennis")) { selectedCategory.value = it }
            Spacer(modifier = Modifier.height(18.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(viewModel.products.value) { ProductCard(it) }
            }
        }
    }
}
