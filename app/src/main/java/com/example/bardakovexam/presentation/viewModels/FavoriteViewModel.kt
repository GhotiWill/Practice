package com.example.bardakovexam.presentation.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bardakovexam.data.models.Product
import com.example.bardakovexam.data.remotes.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val productRepository: ProductRepository
): ViewModel() {
    val products = mutableStateOf<List<Product>>(emptyList())
    val favoriteIds = mutableStateOf<Set<String>>(emptySet())

    init { load() }

    fun load() {
        viewModelScope.launch {
            productRepository.loadFavorites().onSuccess { products.value = it }
            productRepository.loadFavoriteIds().onSuccess { favoriteIds.value = it }
        }
    }

    fun toggleFavorite(productId: String) {
        viewModelScope.launch {
            val isFavorite = favoriteIds.value.contains(productId)
            productRepository.toggleFavorite(productId, isFavorite).onSuccess {
                load()
            }
        }
    }
}
