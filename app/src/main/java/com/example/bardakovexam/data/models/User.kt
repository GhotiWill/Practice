package com.example.bardakovexam.data.models

data class User(
    val id: String,
    val email: String
)


data class Profile(
    val id: String? = null,
    val user_id: String,
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val photo: String? = null
)


data class ActionItem(
    val id: String,
    val photo: String? = null
)
