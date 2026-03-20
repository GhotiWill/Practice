package com.example.bardakovexam.presentation.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bardakovexam.data.models.ActionItem
import com.example.bardakovexam.data.models.Category
import com.example.bardakovexam.data.models.Product
import com.example.bardakovexam.data.remotes.ProductRepository
import com.example.bardakovexam.domain.states.AppState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository
): ViewModel() {
    private val _state = MutableStateFlow<AppState>(AppState.Loading)
    val state = _state.asStateFlow()

    private val _products = mutableStateOf<List<Product>>(emptyList())
    val products = _products

    private val _actions = mutableStateOf<List<ActionItem>>(emptyList())
    val actions = _actions

    private val _categories = mutableStateOf<List<Category>>(emptyList())
    val categories = _categories

    private val _favoriteIds = mutableStateOf<Set<String>>(emptySet())
    val favoriteIds = _favoriteIds

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = AppState.Loading
            productRepository.loadProducts().onSuccess { _products.value = it }
            productRepository.loadActions().onSuccess { _actions.value = it }
            productRepository.loadCategories().onSuccess { _categories.value = it }
            productRepository.loadFavoriteIds().onSuccess { _favoriteIds.value = it }
                .onFailure { _state.value = AppState.Error(Exception(it.message)) }
            _state.value = AppState.Success
        }
    }

    fun toggleFavorite(productId: String) {
        viewModelScope.launch {
            val isFavorite = _favoriteIds.value.contains(productId)
            productRepository.toggleFavorite(productId, isFavorite).onSuccess { nowFavorite ->
                _favoriteIds.value = if (nowFavorite) _favoriteIds.value + productId else _favoriteIds.value - productId
            }
        }
    }
}
