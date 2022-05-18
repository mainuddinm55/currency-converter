package info.learncoding.currencyconverter.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import info.learncoding.currencyconverter.R
import info.learncoding.currencyconverter.data.model.Currency
import info.learncoding.currencyconverter.data.network.DataState
import info.learncoding.currencyconverter.databinding.ActivityMainBinding
import info.learncoding.currencyconverter.utils.gone
import info.learncoding.currencyconverter.utils.hidden
import info.learncoding.currencyconverter.utils.show
import info.learncoding.currencyconverter.utils.showToast
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
        observeConversions()
        observeAmountChange()
        observeSourceCurrencyChange()
    }

    private fun observeAmountChange() {
        binding!!.amountEditText.addTextChangedListener {
            if (it.isNullOrEmpty()) return@addTextChangedListener
            val selectedCurrency = binding!!.currencySpinner.selectedItem as Currency?
            viewModel.convert(
                selectedCurrency?.currency ?: return@addTextChangedListener,
                it.toString().toDouble()
            )
        }
    }

    private fun observeSourceCurrencyChange() {
        binding!!.currencySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val amount = binding!!.amountEditText.text
                    val selectedCurrency = binding!!.currencySpinner.selectedItem as Currency?
                    if (amount.isNullOrEmpty()) return
                    viewModel.convert(
                        selectedCurrency?.currency ?: return,
                        amount.toString().toDouble()
                    )
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
    }

    private fun observeConversions() {
        viewModel.conversions.observe(this) {
            when (it) {
                is DataState.Failed -> {
                    Log.d(TAG, "onCreate: failed ${it.error}")
                    showToast(it.error.message)
                }
                is DataState.Loaded -> {
                    Log.d(TAG, "onCreate: loaded ${it.data}")
                    adapter.differ.submitList(it.data)
                    showProgress(false)
                }
                is DataState.Loading -> {
                    Log.d(TAG, "onCreate: loading")
                    showProgress(true)
                }
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
                is DataState.Loading -> {
                    Log.d(TAG, "onCreate: loading")
                }
            }
        }
    }

    private fun showProgress(isShow: Boolean) {
        if (isShow) {
            binding!!.progressBar.show()
            binding!!.convertedRecyclerView.gone()
        } else {
            binding!!.convertedRecyclerView.show()
            binding!!.progressBar.gone()
        }
    }

    private fun initRecyclerView() {
        binding!!.convertedRecyclerView.apply {
            adapter = this@MainActivity.adapter
        }
    }
}