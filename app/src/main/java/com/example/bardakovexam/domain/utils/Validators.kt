package com.example.bardakovexam.domain.utils

object Validators {
    private val emailRegex = Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{3,}$")
    fun isValidEmail(email: String): Boolean = emailRegex.matches(email)
}
