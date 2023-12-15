package com.example.yourjourney.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.yourjourney.data.dcResponse
import com.example.yourjourney.data.dcLogin
import com.example.yourjourney.repo.AuthRepository
import com.example.yourjourney.response.ResponseAPI


@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel() {

    fun loginUser(loginBody: dcLogin): LiveData<ResponseAPI<dcResponse>> {
        val result = MutableLiveData<ResponseAPI<dcResponse>>()
        viewModelScope.launch {
            authRepository.loginUser(loginBody).collect {
                result.postValue(it)
            }
        }
        return result
    }

}