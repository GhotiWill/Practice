package com.example.bardakovexam.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bardakovexam.data.remotes.UserRepository
import com.example.bardakovexam.domain.states.AppState
import com.example.bardakovexam.domain.utils.Validators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow<AppState>(AppState.Idle)
    val state = _state.asStateFlow()

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _state.value = AppState.Error(IllegalArgumentException("Заполните все поля"))
            return
        }
        if (!Validators.isValidEmail(email)) {
            _state.value = AppState.Error(IllegalArgumentException("Некорректный email"))
            return
        }
        viewModelScope.launch {
            _state.value = AppState.Loading
            userRepository.signIn(email, password)
                .onSuccess { _state.value = AppState.Success }
                .onFailure { _state.value = AppState.Error(Exception(it.message ?: "Ошибка авторизации")) }
        }
    }

    fun clearError() {
        if (_state.value is AppState.Error) _state.value = AppState.Idle
    }
}
