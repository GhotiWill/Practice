package com.example.bardakovexam.data.models

data class User(
    val id: String,
    val email: String,
    val profileId: String? = null,
    val name: String? = null,
    val password: String? = null,
    val photo: String? = null
)


data class ActionItem(
    val id: String,
    val photo: String? = null
)
