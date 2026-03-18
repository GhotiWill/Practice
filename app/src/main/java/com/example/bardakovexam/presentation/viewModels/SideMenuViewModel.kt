package com.example.bardakovexam.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bardakovexam.data.remotes.UserRepository
import com.example.bardakovexam.domain.states.AppState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SideMenuViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = MutableStateFlow<AppState>(AppState.Idle)
    val state = _state.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            userRepository.signOut()
                .onSuccess { _state.value = AppState.Success }
                .onFailure { _state.value = AppState.Error(Exception(it.message)) }
        }
    }
}
