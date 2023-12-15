package com.example.yourjourney.remote


import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.yourjourney.response.ResponseAPI
import com.example.yourjourney.response.ResponseAddJourney
import com.example.yourjourney.response.ResponseGetJourney
import com.example.yourjourney.data.JourneyService
import com.example.yourjourney.database.AppDatabase
import com.example.yourjourney.database.JourneyDb
import com.example.yourjourney.extention.Value.DEFAULT_PAGE_SIZE
import com.example.yourjourney.mediator.JourneyMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalPagingApi
@Singleton
class JourneyRemoteDataSource @Inject constructor(
    private val storyAppDatabase: AppDatabase,
    private val storyService: JourneyService
) {

    fun getAllJourney(token: String): Flow<PagingData<JourneyDb>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE
            ),
            remoteMediator = JourneyMediator(storyAppDatabase, storyService, token),
            pagingSourceFactory = { storyAppDatabase.getStoryDao().getAllStories() }
        ).flow
    }

    suspend fun getJourneyWithLocation(token: String, location: Int): Flow<ResponseAPI<ResponseGetJourney>> {
        return flow {
            try {
                emit(ResponseAPI.Loading)
                val response = storyService.getAllJourney(token, location)
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

    suspend fun addNewJourney(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): Flow<ResponseAPI<ResponseAddJourney>> {
        return flow {
            try {
                emit(ResponseAPI.Loading)
                val response = storyService.addNewJourney(token, file, description, lat, lon)
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