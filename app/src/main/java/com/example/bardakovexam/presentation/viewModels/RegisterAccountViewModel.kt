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
class RegisterAccountViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow<AppState>(AppState.Idle)
    val state = _state.asStateFlow()

    fun register(name: String, email: String, password: String) {
        if (name.isBlank()) {
            _state.value = AppState.Error(IllegalArgumentException("Введите имя"))
            return
        }
        if (!Validators.isValidEmail(email)) {
            _state.value = AppState.Error(IllegalArgumentException("Некорректный email"))
            return
        }
        if (password.isBlank()) {
            _state.value = AppState.Error(IllegalArgumentException("Введите пароль"))
            return
        }
        viewModelScope.launch {
            _state.value = AppState.Loading
            userRepository.signUp(name, email, password)
                .onSuccess { _state.value = AppState.Success }
                .onFailure { _state.value = AppState.Error(Exception(it.message ?: "Ошибка регистрации")) }
        }
    }

    fun clearError() {
        if (_state.value is AppState.Error) _state.value = AppState.Idle
    }
}
