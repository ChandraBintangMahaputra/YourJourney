package com.example.yourjourney.data

import com.google.gson.annotations.SerializedName
import com.example.yourjourney.identity.User

data class dcResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("loginResult")
    val loginResult: User
)
