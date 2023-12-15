package com.example.yourjourney.response


import com.example.yourjourney.identity.Journey
import com.google.gson.annotations.SerializedName

data class ResponseGetJourney(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("listStory")
    val listStory: List<Journey>
)