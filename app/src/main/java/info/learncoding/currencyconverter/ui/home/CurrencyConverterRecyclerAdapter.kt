package info.learncoding.currencyconverter.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import info.learncoding.currencyconverter.data.model.ConversionRate
import info.learncoding.currencyconverter.databinding.RowItemCurrencyResultBinding
import info.learncoding.currencyconverter.ui.base.BaseRecyclerAdapter
import javax.inject.Inject

class CurrencyConverterRecyclerAdapter @Inject constructor(
) : BaseRecyclerAdapter<ConversionRate, RowItemCurrencyResultBinding>() {

    override fun initializeViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): RowItemCurrencyResultBinding {
        return RowItemCurrencyResultBinding.inflate(layoutInflater, parent, false)
    }

    override fun initializeDiffItemCallback(): DiffUtil.ItemCallback<ConversionRate> {
        return object : DiffUtil.ItemCallback<ConversionRate>() {
            override fun areItemsTheSame(
                oldItem: ConversionRate,
                newItem: ConversionRate
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ConversionRate,
                newItem: ConversionRate
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<RowItemCurrencyResultBinding>,
        position: Int
    ) {
        val item = differ.currentList[position]
        holder.binding.currency = item
    }
}