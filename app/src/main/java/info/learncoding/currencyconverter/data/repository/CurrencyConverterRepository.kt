package info.learncoding.currencyconverter.data.repository

import androidx.lifecycle.LiveData
import info.learncoding.currencyconverter.data.model.Conversion
import info.learncoding.currencyconverter.data.model.Currency
import info.learncoding.currencyconverter.data.network.DataState

interface CurrencyConverterRepository {

    fun getSupportedCurrencies(): LiveData<DataState<List<Currency>>>

    fun getCurrencyRate(source: String,amount:Double): LiveData<DataState<List<Conversion>>>

}