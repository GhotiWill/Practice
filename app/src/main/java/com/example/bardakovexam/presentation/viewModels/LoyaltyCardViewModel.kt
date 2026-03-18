package com.example.bardakovexam.presentation.viewModels

import androidx.lifecycle.ViewModel
import com.example.bardakovexam.data.remotes.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoyaltyCardViewModel @Inject constructor(): ViewModel() {
    val userId = SessionManager.userId.orEmpty()
}
