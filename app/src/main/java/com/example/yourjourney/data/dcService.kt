package com.example.yourjourney.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface dcService {
    @POST("register")
    suspend fun registerUser(
        @Body authBody: dcRegister
    ): Response<dcResponse>

    @POST("login")
    suspend fun loginUser(
        @Body loginBody: dcLogin
    ): dcResponse

}