package info.learncoding.currencyconverter.data.repository

import androidx.lifecycle.LiveData
import info.learncoding.currencyconverter.data.local.AppDatabase
import info.learncoding.currencyconverter.data.model.ApiError
import info.learncoding.currencyconverter.data.model.Conversion
import info.learncoding.currencyconverter.data.model.Currency
import info.learncoding.currencyconverter.data.model.ErrorType
import info.learncoding.currencyconverter.data.network.ApiClient
import info.learncoding.currencyconverter.data.network.BaseRepositoryImp
import info.learncoding.currencyconverter.data.network.DataState
import info.learncoding.currencyconverter.data.network.NetworkResourceBounce
import info.learncoding.currencyconverter.data.network.response.ApiResponse
import info.learncoding.currencyconverter.utils.AppConstraint
import java.util.*
import java.util.concurrent.TimeUnit

class CurrencyConverterRepositoryImp(
    private val apiClient: ApiClient,
    private val database: AppDatabase
) : BaseRepositoryImp(), CurrencyConverterRepository {

    /**
     * Get supported currencies
     * If available on local and not extend max refresh than return from local
     * Otherwise fetch from network
     */
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

    /**
     * Convert given amount from source currency to all other currency
     * @param source currency convert will be based on this currency
     * @param amount how many amount will convert
     */
    override fun convertCurrency(
        source: String,
        amount: Double
    ): LiveData<DataState<List<Conversion>>> {
        return object : NetworkResourceBounce<List<Conversion>>() {
            override suspend fun query(): List<Conversion> {
                return database.conversionDao().getLast()
            }

            override fun queryObservable(): LiveData<List<Conversion>> {
                return database.conversionDao().convertCurrency(source, amount)
            }

            override suspend fun fetch(): ApiResponse<List<Conversion>> {
                return when (val response = safeApiCall { apiClient.getConversionRate() }) {
                    is ApiResponse.Error -> response
                    is ApiResponse.Success -> {
                        if (response.data.success) {
                            val rates = response.data.quotes.map {
                                val currency = it.key.removePrefix("USD")
                                Conversion(
                                    source = response.data.source,
                                    currency = currency,
                                    amount = it.value
                                )
                            }
                            ApiResponse.Success(rates)
                        } else {
                            ApiResponse.Error(ApiError(ErrorType.FAILURE, "Something went wrong"))
                        }
                    }
                }
            }

            override suspend fun saveFetchResult(data: List<Conversion>) {
                database.conversionDao().deleteAll()
                database.conversionDao().insert(data)
            }

            override fun shouldFetch(data: List<Conversion>?): Boolean {
                return data.isNullOrEmpty() || needToFetchData(data.first().createdAt)
            }

        }.asLiveData()
    }

    private fun needToFetchData(lastFetchTime: Date): Boolean {
        val diff = Date().time - lastFetchTime.time
        return TimeUnit.MILLISECONDS.toMinutes(diff) >= AppConstraint.MAX_CACHED_TIME
    }

}