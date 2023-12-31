package com.example.yourjourney.data

import com.example.yourjourney.response.ResponseAddJourney
import com.example.yourjourney.response.ResponseGetJourney
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface JourneyService {
    @GET("stories")
    suspend fun getAllJourney(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ResponseGetJourney

    @GET("stories")
    suspend fun getAllJourney(
        @Header("Authorization") token: String,
        @Query("location") location: Int
    ): ResponseGetJourney

    @Multipart
    @POST("stories")
    suspend fun addNewJourney(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): ResponseAddJourney


}