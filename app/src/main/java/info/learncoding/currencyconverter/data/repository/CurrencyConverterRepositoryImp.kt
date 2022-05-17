package info.learncoding.currencyconverter.data.repository

import androidx.lifecycle.LiveData
import info.learncoding.currencyconverter.data.local.AppDatabase
import info.learncoding.currencyconverter.data.model.ApiError
import info.learncoding.currencyconverter.data.model.Currency
import info.learncoding.currencyconverter.data.model.ErrorType
import info.learncoding.currencyconverter.data.network.ApiClient
import info.learncoding.currencyconverter.data.network.BaseRepositoryImp
import info.learncoding.currencyconverter.data.network.DataState
import info.learncoding.currencyconverter.data.network.NetworkResourceBounce
import info.learncoding.currencyconverter.data.network.response.ApiResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import java.util.*
import java.util.concurrent.TimeUnit

class CurrencyConverterRepositoryImp(
    private val apiClient: ApiClient,
    private val database: AppDatabase
) : BaseRepositoryImp(), CurrencyConverterRepository {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    override fun getSupportedCurrencies(): LiveData<DataState<List<Currency>>> {
        return object : NetworkResourceBounce<List<Currency>>() {

            override fun shouldFetch(data: List<Currency>?): Boolean {
                return data.isNullOrEmpty() || needToFetchData(data.first().createdAt)
            }

            override suspend fun query(): List<Currency> {
                return database.currencyDao().getLast()
            }

            override fun queryObservable(): LiveData<List<Currency>> {
                return database.currencyDao().getAll()
            }

            override suspend fun fetch(): ApiResponse<List<Currency>> {
                return when (val response = safeApiCall { apiClient.getSupportedCurrencies() }) {
                    is ApiResponse.Error -> response
                    is ApiResponse.Success -> {
                        if (response.data.success) {
                            ApiResponse.Success(response.data.currencies?.map {
                                Currency(currency = it.key, country = it.value)
                            } ?: listOf())
                        } else {
                            ApiResponse.Error(ApiError(ErrorType.FAILURE, "Something went wrong"))
                        }
                    }
                }
            }

            override suspend fun saveFetchResult(data: List<Currency>) {
                database.currencyDao().deleteAll()
                database.currencyDao().insert(data)
            }

        }.asLiveData()

    }

    private fun needToFetchData(lastFetchTime: Date): Boolean {
        val diff = Date().time - lastFetchTime.time
        return TimeUnit.MILLISECONDS.toMinutes(diff) >= 30
    }

    override fun clearScope() {
        scope.cancel()
    }
}