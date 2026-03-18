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
class CatalogViewModel @Inject constructor(
    private val productRepository: ProductRepository
): ViewModel() {
    val products = mutableStateOf<List<Product>>(emptyList())

    init {
        viewModelScope.launch { productRepository.loadProducts().onSuccess { products.value = it } }
    }

    fun addFavorite(productId: String) {
        viewModelScope.launch { productRepository.addFavorite(productId) }
    }
}
