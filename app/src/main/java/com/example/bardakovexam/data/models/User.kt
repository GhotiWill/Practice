package com.example.bardakovexam.data.models

data class User(
    val id: String,
    val email: String,
    val profileId: String? = null,
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
