package info.learncoding.currencyconverter.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import info.learncoding.currencyconverter.data.model.Conversion
import info.learncoding.currencyconverter.data.network.DataState
import info.learncoding.currencyconverter.data.repository.CurrencyConverterRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val currencyConverterRepository: CurrencyConverterRepository
) : ViewModel() {

    val supportedCurrencies = currencyConverterRepository.getSupportedCurrencies()

    private val _currencyConversions: MediatorLiveData<DataState<List<Conversion>>> =
        MediatorLiveData()
    val conversions: LiveData<DataState<List<Conversion>>> = _currencyConversions

    fun convert(source: String, amount: Double) {
        val conversionsLiveData = currencyConverterRepository.getCurrencyRate(source, amount)
        _currencyConversions.addSource(conversionsLiveData) {
            _currencyConversions.postValue(it)
        }
    }

}
