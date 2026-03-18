package com.example.bardakovexam.presentation.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bardakovexam.data.models.Profile
import com.example.bardakovexam.data.remotes.SessionManager
import com.example.bardakovexam.data.remotes.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    val profile = mutableStateOf(Profile(user_id = SessionManager.userId.orEmpty()))

    init {
        viewModelScope.launch {
            userRepository.loadProfile().onSuccess { if (it != null) profile.value = it }
        }
    }

    fun save() {
        viewModelScope.launch { userRepository.saveProfile(profile.value) }
    }
}
