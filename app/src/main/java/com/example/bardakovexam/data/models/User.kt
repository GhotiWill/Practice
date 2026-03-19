package com.example.bardakovexam.data.models

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    val email: String
)


data class Profile(
    val id: String? = null,
    val user_id: String,
    @SerializedName(value = "firstname", alternate = ["name"])
    val name: String? = null,
    @SerializedName(value = "address", alternate = ["email"])
    val email: String? = null,
    @SerializedName(value = "phone", alternate = ["password"])
    val password: String? = null,
    val photo: String? = null
)


data class ActionItem(
    val id: String,
    val photo: String? = null
)
