package com.example.bardakovexam.domain.states

sealed class AppState {
    data object Idle : AppState()
    data object Loading: AppState()
    data object Success: AppState()
    data class Error(val ex: Exception): AppState()
}
