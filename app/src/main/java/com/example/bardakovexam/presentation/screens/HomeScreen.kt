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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bardakovexam.data.models.ActionItem
import com.example.bardakovexam.domain.states.AppState
import com.example.bardakovexam.presentation.navigation.navRoutes
import com.example.bardakovexam.presentation.viewModels.HomeViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val products = viewModel.products.value
    val categories = viewModel.categories.value
    val favoriteIds = viewModel.favoriteIds.value
    val selectedCategory = remember(categories) { mutableStateOf("Все") }
    val filteredProducts = products.filter { selectedCategory.value == "Все" || categories.firstOrNull { category -> category.id == it.categoryId }?.title == selectedCategory.value }

    Box(modifier = Modifier.fillMaxSize().background(AppBackground)) {
        when (state) {
            is AppState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = AppBlue) }
            is AppState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text((state as AppState.Error).ex.message.orEmpty()) }
            else -> {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Text("Главная", color = AppText, fontSize = 28.sp, fontWeight = FontWeight.Normal)
                    }
                    Spacer(modifier = Modifier.height(22.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color.White, shape = RoundedCornerShape(18.dp), modifier = Modifier.weight(1f)) {
                            Row(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("⌕", color = AppMuted, fontSize = 22.sp)
                                Text("Поиск", color = AppMuted, fontSize = 16.sp, modifier = Modifier.padding(start = 10.dp))
                            }
                        }
                        Box(modifier = Modifier.size(62.dp).background(AppBlue, CircleShape), contentAlignment = Alignment.Center) { Text("☷", color = Color.White, fontSize = 24.sp) }
                    }
                    Spacer(modifier = Modifier.height(26.dp))
                    Text("Категории", color = AppText, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    CategoryChips(selectedCategory.value, listOf("Все") + categories.map { it.title }) { selectedCategory.value = it }
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeader("Популярное")
                    Spacer(modifier = Modifier.height(14.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filteredProducts.take(2)) { product ->
                            ProductCard(
                                product = product,
                                isFavorite = favoriteIds.contains(product.id),
                                onFavoriteToggle = { viewModel.toggleFavorite(product.id) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(22.dp))
                    SectionHeader("Акции")
                    Spacer(modifier = Modifier.height(14.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(viewModel.actions.value.ifEmpty { listOf(ActionItem("stub", null)) }) { action ->
                            Surface(color = Color.White, shape = RoundedCornerShape(24.dp), modifier = Modifier.fillParentMaxWidth()) {
                                if (action.photo != null) {
                                    AsyncImage(model = action.photo, contentDescription = null, modifier = Modifier.fillMaxWidth().height(116.dp).clip(RoundedCornerShape(24.dp)))
                                } else {
                                    Row(modifier = Modifier.fillMaxWidth().height(116.dp).padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column {
                                            Text("Summer Sale", color = AppText, fontSize = 14.sp)
                                            Text("15% OFF", color = Color(0xFF6A4AD1), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Text("👟", fontSize = 52.sp)
                                    }
                                }
                            }
                        }
                    }
                }
                Box(modifier = Modifier.align(Alignment.BottomCenter)) { BottomNavBar(navRoutes.home, navController) }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, color = AppText, fontSize = 20.sp)
        Text("Все", color = AppBlue, fontSize = 16.sp)
    }
}
