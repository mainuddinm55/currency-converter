package info.learncoding.currencyconverter.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import info.learncoding.currencyconverter.R
import info.learncoding.currencyconverter.data.model.Currency
import info.learncoding.currencyconverter.data.network.DataState
import info.learncoding.currencyconverter.databinding.ActivityMainBinding
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var binding: ActivityMainBinding? = null

    @Inject
    lateinit var adapter: CurrencyConverterRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initRecyclerView()
        observeSupportedCurrencies()

        viewModel.conversionRates.observe(this) {
            when (it) {
                is DataState.Failed -> Log.d(TAG, "onCreate: failed ${it.error}")
                is DataState.Loaded -> {
                    Log.d(TAG, "onCreate: loaded ${it.data}")
                    adapter.differ.submitList(it.data)
                }
                is DataState.Loading -> Log.d(TAG, "onCreate: loading")
            }
        }
    }

    private fun observeSupportedCurrencies() {
        viewModel.supportedCurrencies.observe(this) {
            when (it) {
                is DataState.Failed -> Log.d(TAG, "onCreate: failed ${it.error}")
                is DataState.Loaded -> {
                    Log.d(TAG, "onCreate: loaded")
                    val arrayAdapter = ArrayAdapter<Currency>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        it.data
                    )
                    binding!!.currencySpinner.adapter = arrayAdapter
                }
                is DataState.Loading -> Log.d(TAG, "onCreate: loading")
            }
        }
    }

    private fun initRecyclerView() {
        binding!!.convertedRecyclerView.apply {
            adapter = this@MainActivity.adapter
        }
    }
}