package com.example.yourjourney.response

import com.google.gson.annotations.SerializedName

data class ResponseAddJourney(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)
