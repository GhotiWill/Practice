package com.example.bardakovexam.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.example.bardakovexam.data.remotes.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductCardViewModel @Inject constructor(
    private val productRepository: ProductRepository
): ViewModel() {
    suspend fun addFavorite(productId: String) {
        productRepository.addFavorite(productId)
    }
}
