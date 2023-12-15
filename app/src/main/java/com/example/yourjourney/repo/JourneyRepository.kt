package com.example.yourjourney.repo


import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.example.yourjourney.database.JourneyDb
import com.example.yourjourney.remote.JourneyRemoteDataSource
import com.example.yourjourney.response.ResponseAPI
import com.example.yourjourney.response.ResponseAddJourney
import com.example.yourjourney.response.ResponseGetJourney
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalPagingApi
@Singleton
class JourneyRepository @Inject constructor(private val storyDataSource: JourneyRemoteDataSource) {

    fun getAllJourney(token: String): Flow<PagingData<JourneyDb>> = storyDataSource.getAllJourney(token).flowOn(Dispatchers.IO)

    suspend fun getJourneyWithLocation(token: String, location: Int): Flow<ResponseAPI<ResponseGetJourney>> =
        storyDataSource.getJourneyWithLocation(token, location).flowOn(Dispatchers.IO)

    suspend fun addNewJourney(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Flow<ResponseAPI<ResponseAddJourney>> {
        return storyDataSource.addNewJourney(token, file, description, lat, lon)
    }
}




