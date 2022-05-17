package info.learncoding.currencyconverter.data.network

import info.learncoding.currencyconverter.data.model.ApiError

sealed class DataState<out T> {
    data class Loading<out T>(val data: T? = null) : DataState<T>()
    data class Loaded<out T>(val data: T) : DataState<T>()
    data class Failed<out T>(val error: ApiError) : DataState<T>()
}
