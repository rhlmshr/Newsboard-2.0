package com.newsboard.utils.base

sealed class ResponseState<out T : Any> {
    data class Success<out T : Any>(val output: T) : ResponseState<T>()
    data class Error(val message: String = "") : ResponseState<Nothing>()

    object Loading : ResponseState<Nothing>()
    object NoInternet : ResponseState<Nothing>()
    object NoData : ResponseState<Nothing>()
}