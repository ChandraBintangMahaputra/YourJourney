package com.example.yourjourney.model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.yourjourney.database.JourneyDb
import com.example.yourjourney.response.ResponseAPI
import com.example.yourjourney.response.ResponseGetJourney
import com.example.yourjourney.response.ResponseAddJourney
import com.example.yourjourney.repo.JourneyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class JourneyViewModel @Inject constructor(private val storyRepository: JourneyRepository): ViewModel() {

    fun getStoriesWithLocation(token: String, location: Int) : LiveData<ResponseAPI<ResponseGetJourney>> {
        val result = MutableLiveData<ResponseAPI<ResponseGetJourney>>()
        viewModelScope.launch {
            storyRepository.getJourneyWithLocation(token, location).collect {
                result.postValue(it)
            }
        }
        return result
    }

    fun getAllJourney(token: String): LiveData<PagingData<JourneyDb>> =
        storyRepository.getAllJourney(token).cachedIn(viewModelScope).asLiveData()

    fun addNewJourney(token: String, file: MultipartBody.Part, description: RequestBody, lat: RequestBody?, lon: RequestBody?): LiveData<ResponseAPI<ResponseAddJourney>> {
        val result = MutableLiveData<ResponseAPI<ResponseAddJourney>>()
        viewModelScope.launch {
            storyRepository.addNewJourney(token, file, description, lat, lon).collect {
                result.postValue(it)
            }
        }
        return result
    }

}
