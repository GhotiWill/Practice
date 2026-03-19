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
    val profile = mutableStateOf(Profile(user_id = SessionManager.userId.orEmpty(), email = SessionManager.email))
    val errorMessage = mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            userRepository.loadProfile()
                .onSuccess { if (it != null) profile.value = it }
                .onFailure { errorMessage.value = it.message }
        }
    }

    fun save(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            userRepository.saveProfile(profile.value)
                .onSuccess {
                    errorMessage.value = null
                    onComplete?.invoke()
                }
                .onFailure {
                    errorMessage.value = it.message
                }
        }
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            userRepository.signOut()
                .onSuccess {
                    errorMessage.value = null
                    onComplete()
                }
                .onFailure {
                    errorMessage.value = it.message
                }
        }
    }
}
