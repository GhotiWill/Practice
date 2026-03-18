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
class ForgotPasswordViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow<AppState>(AppState.Idle)
    val state = _state.asStateFlow()

    var email: String = ""

    fun sendCode() {
        if (!Validators.isValidEmail(email)) {
            _state.value = AppState.Error(IllegalArgumentException("Некорректный email"))
            return
        }
        viewModelScope.launch {
            _state.value = AppState.Loading
            userRepository.sendRecovery(email)
                .onSuccess { _state.value = AppState.Success }
                .onFailure { _state.value = AppState.Error(Exception(it.message)) }
        }
    }
}
