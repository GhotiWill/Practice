package com.example.bardakovexam.data.remotes

object SessionManager {
    var accessToken: String? = null
    var userId: String? = null
    var email: String? = null

    fun clear() {
        accessToken = null
        userId = null
        email = null
    }
}
