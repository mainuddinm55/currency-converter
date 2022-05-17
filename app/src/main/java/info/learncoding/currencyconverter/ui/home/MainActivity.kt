package info.learncoding.currencyconverter.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import info.learncoding.currencyconverter.R
import info.learncoding.currencyconverter.data.model.Currency
import info.learncoding.currencyconverter.data.network.DataState

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner: Spinner = findViewById(R.id.currency_spinner)

        viewModel.supportedCurrencies.observe(this) {
            when (it) {
                is DataState.Failed -> Log.d(TAG, "onCreate: failed ${it.error}")
                is DataState.Loaded -> {
                    Log.d(TAG, "onCreate: loaded")
                    val adapter = ArrayAdapter<Currency>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        it.data
                    )
                    spinner.adapter = adapter
                }
                is DataState.Loading -> Log.d(TAG, "onCreate: loading")
            }
        }
    }
}