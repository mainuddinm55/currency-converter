package info.learncoding.currencyconverter.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import info.learncoding.currencyconverter.data.repository.CurrencyConverterRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val currencyConverterRepository: CurrencyConverterRepository
) : ViewModel() {

    val supportedCurrencies = currencyConverterRepository.getSupportedCurrencies()

    override fun onCleared() {
        super.onCleared()

        currencyConverterRepository.clearScope()
    }
}
