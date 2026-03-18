package com.example.bardakovexam.data.models

data class User(
    val id: String,
    val email: String
)


data class Profile(
    val id: String? = null,
    val user_id: String,
    val firstname: String? = null,
    val lastname: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val photo: String? = null
)


data class ActionItem(
    val id: String,
    val photo: String? = null
)
