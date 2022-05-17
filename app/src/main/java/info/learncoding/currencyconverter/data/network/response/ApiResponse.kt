package info.learncoding.currencyconverter.data.network.response

import info.learncoding.currencyconverter.data.model.ApiError


sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val error: ApiError) : ApiResponse<Nothing>()
}
