package info.learncoding.currencyconverter.data.network

import info.learncoding.currencyconverter.data.model.ApiError
import info.learncoding.currencyconverter.data.model.ErrorType
import info.learncoding.currencyconverter.data.network.response.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

abstract class BaseRepositoryImp {
    suspend fun <T> safeApiCall(apiCall: suspend () -> T): ApiResponse<T> {
        return withContext(Dispatchers.IO) {
            try {
                ApiResponse.Success(apiCall.invoke())
            } catch (e: Exception) {
                val errorType: ErrorType
                var message = "Something went wrong, please try again"
                e.printStackTrace()
                when (e) {
                    is IOException -> {
                        errorType = ErrorType.NETWORK
                        message = e.message ?: "No internet connection"
                    }
                    is HttpException -> {
                        when (e.code()) {
                            400 -> {
                                errorType = ErrorType.FAILURE
                                message = "Invalid request, please try again"
                            }
                            204 -> {
                                errorType = ErrorType.FAILURE
                                message = "No Data Found"
                            }
                            401 -> {
                                errorType = ErrorType.UNAUTHORIZED
                                message = "Unauthorized"
                            }
                            403 -> {
                                errorType = ErrorType.FORBIDDEN
                                message = "Forbidden"
                            }
                            404 -> {
                                errorType = ErrorType.NOT_FOUND
                                message = "Request not found"
                            }
                            422 -> {
                                errorType = ErrorType.VALIDATION_ERROR
                                message = "Missing required field"
                            }
                            500 -> {
                                errorType = ErrorType.INTERNAL_SERVER_ERROR
                                message = "Internal server error"
                            }
                            else -> {
                                errorType = ErrorType.FAILURE
                                message = e.message()
                            }
                        }
                    }
                    else -> {
                        errorType = ErrorType.FAILURE
                    }
                }
                ApiResponse.Error(ApiError(errorType, message))

            }
        }
    }
}