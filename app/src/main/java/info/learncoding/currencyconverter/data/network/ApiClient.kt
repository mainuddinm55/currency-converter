package info.learncoding.currencyconverter.data.network

import info.learncoding.currencyconverter.data.network.response.CurrencyConversionRateResponse
import info.learncoding.currencyconverter.data.network.response.SupportedCurrenciesResponse
import retrofit2.http.GET

interface ApiClient {

    @GET("list")
    suspend fun getSupportedCurrencies(): SupportedCurrenciesResponse

    @GET("live")
    suspend fun getConversionRate(): CurrencyConversionRateResponse

}