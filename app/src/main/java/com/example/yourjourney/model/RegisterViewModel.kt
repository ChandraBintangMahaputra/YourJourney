package com.example.yourjourney.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
import com.example.yourjourney.data.dcResponse
import com.example.yourjourney.data.dcRegister
import com.example.yourjourney.repo.AuthRepository
import com.example.yourjourney.response.ResponseAPI

@HiltViewModel
class RegisterViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {

    fun registerUser(authBody: dcRegister): LiveData<ResponseAPI<Response<dcResponse>>> {
        val result = MutableLiveData<ResponseAPI<Response<dcResponse>>>()
        viewModelScope.launch {
            authRepository.registerUser(authBody).collect {
                result.postValue(it)
            }
        }
        return result
    }

}