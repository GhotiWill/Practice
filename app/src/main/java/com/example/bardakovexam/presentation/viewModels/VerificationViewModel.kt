package com.example.bardakovexam.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bardakovexam.data.remotes.UserRepository
import com.example.bardakovexam.domain.states.AppState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow<AppState>(AppState.Idle)
    val state = _state.asStateFlow()

    private val _seconds = MutableStateFlow(60)
    val seconds = _seconds.asStateFlow()

    init { startTimer() }

    fun startTimer() {
        viewModelScope.launch {
            _seconds.value = 60
            while (_seconds.value > 0) {
                delay(1000)
                _seconds.value -= 1
            }
        }
    }

    fun verify(email: String, code: String) {
        viewModelScope.launch {
            _state.value = AppState.Loading
            userRepository.verifyRecovery(email, code)
                .onSuccess { _state.value = AppState.Success }
                .onFailure { _state.value = AppState.Error(Exception(it.message)) }
        }
    }
}
