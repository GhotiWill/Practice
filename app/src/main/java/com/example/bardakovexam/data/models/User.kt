package com.example.bardakovexam.data.models

import com.google.gson.annotations.SerializedName

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
