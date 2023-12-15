package com.example.yourjourney.repo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton
import com.example.yourjourney.data.dcResponse
import com.example.yourjourney.data.dcLogin
import com.example.yourjourney.data.dcService
import com.example.yourjourney.data.dcRegister
import com.example.yourjourney.remote.AuthRemoteDataSource
import com.example.yourjourney.response.ResponseAPI

@Singleton
class AuthRepository @Inject constructor(private val authDataSource: AuthRemoteDataSource) {

    suspend fun registerUser(authBody: dcRegister): Flow<ResponseAPI<Response<dcResponse>>> {
        return authDataSource.registerUser(authBody).flowOn(Dispatchers.IO)
    }

    suspend fun loginUser(loginBody: dcLogin): Flow<ResponseAPI<dcResponse>> {
        return authDataSource.loginUser(loginBody).flowOn(Dispatchers.IO)
    }

}