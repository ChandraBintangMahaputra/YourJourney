package com.example.yourjourney.response

sealed class ResponseAPI<out R> {
    data class Success<out T>(val data: T) : ResponseAPI<T>()
    data class Error(val errorMessage: String) : ResponseAPI<Nothing>()
    object Loading : ResponseAPI<Nothing>()
    object Empty : ResponseAPI<Nothing>()
}
