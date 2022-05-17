package info.learncoding.currencyconverter.data.repository

import androidx.lifecycle.LiveData
import info.learncoding.currencyconverter.data.model.Currency
import info.learncoding.currencyconverter.data.network.DataState

interface CurrencyConverterRepository {

    fun getSupportedCurrencies(): LiveData<DataState<List<Currency>>>

    fun clearScope()

}