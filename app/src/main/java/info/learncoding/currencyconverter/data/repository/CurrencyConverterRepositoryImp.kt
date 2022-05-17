package info.learncoding.currencyconverter.data.repository

import androidx.lifecycle.LiveData
import info.learncoding.currencyconverter.data.local.AppDatabase
import info.learncoding.currencyconverter.data.model.ApiError
import info.learncoding.currencyconverter.data.model.ConversionRate
import info.learncoding.currencyconverter.data.model.Currency
import info.learncoding.currencyconverter.data.model.ErrorType
import info.learncoding.currencyconverter.data.network.ApiClient
import info.learncoding.currencyconverter.data.network.BaseRepositoryImp
import info.learncoding.currencyconverter.data.network.DataState
import info.learncoding.currencyconverter.data.network.NetworkResourceBounce
import info.learncoding.currencyconverter.data.network.response.ApiResponse
import java.util.*
import java.util.concurrent.TimeUnit

class CurrencyConverterRepositoryImp(
    private val apiClient: ApiClient,
    private val database: AppDatabase
) : BaseRepositoryImp(), CurrencyConverterRepository {

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

    override fun getCurrencyRate(
        source: String,
        amount: Double
    ): LiveData<DataState<List<ConversionRate>>> {
        return object : NetworkResourceBounce<List<ConversionRate>>() {
            override suspend fun query(): List<ConversionRate> {
                return database.conversionRateDao().getLast()
            }

            override fun queryObservable(): LiveData<List<ConversionRate>> {
                return database.conversionRateDao().convertCurrency(source, amount)
            }

            override suspend fun fetch(): ApiResponse<List<ConversionRate>> {
                return when (val response = safeApiCall { apiClient.getConversionRate() }) {
                    is ApiResponse.Error -> response
                    is ApiResponse.Success -> {
                        if (response.data.success) {
                            val rates = response.data.quotes.map {
                                val currency = it.key.removePrefix("USD")
                                ConversionRate(
                                    source = response.data.source,
                                    currency = currency,
                                    rate = it.value
                                )
                            }
                            ApiResponse.Success(rates)
                        } else {
                            ApiResponse.Error(ApiError(ErrorType.FAILURE, "Something went wrong"))
                        }
                    }
                }
            }

            override suspend fun saveFetchResult(data: List<ConversionRate>) {
                database.conversionRateDao().deleteAll()
                database.conversionRateDao().insert(data)
            }

            override fun shouldFetch(data: List<ConversionRate>?): Boolean {
                return data.isNullOrEmpty() || needToFetchData(data.first().createdAt)
            }

        }.asLiveData()
    }

    private fun needToFetchData(lastFetchTime: Date): Boolean {
        val diff = Date().time - lastFetchTime.time
        return TimeUnit.MILLISECONDS.toMinutes(diff) >= 30
    }

}