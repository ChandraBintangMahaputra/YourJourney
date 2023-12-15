package com.example.yourjourney.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton
import com.example.yourjourney.response.ResponseAPI
import com.example.yourjourney.data.dcLogin
import com.example.yourjourney.data.dcRegister
import com.example.yourjourney.data.dcService
import com.example.yourjourney.data.dcResponse

@Singleton
class AuthRemoteDataSource @Inject constructor(private val authService: dcService) {

    suspend fun registerUser(authBody: dcRegister): Flow<ResponseAPI<Response<dcResponse>>> {
        return flow {
            try {
                emit(ResponseAPI.Loading)
                val response = authService.registerUser(authBody)
                if (response.code() == 201) {
                    emit(ResponseAPI.Success(response))
                } else if (response.code() == 400) {
                    val errorBody = JSONObject(response.errorBody()!!.string())
                    emit(ResponseAPI.Error(errorBody.getString("message")))
                }
            } catch (ex: Exception) {
                emit(ResponseAPI.Error(ex.message.toString()))
            }
        }
    }

    suspend fun loginUser(loginBody: dcLogin): Flow<ResponseAPI<dcResponse>> {
        return flow {
            try {
                emit(ResponseAPI.Loading)
                val response = authService.loginUser(loginBody)
                if (!response.error) {
                    emit(ResponseAPI.Success(response))
                } else {
                    emit(ResponseAPI.Error(response.message))
                }
            } catch (ex: Exception) {
                emit(ResponseAPI.Error(ex.message.toString()))
            }
        }
    }

}