package info.learncoding.currencyconverter.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import info.learncoding.currencyconverter.data.model.Conversion
import info.learncoding.currencyconverter.databinding.RowItemCurrencyResultBinding
import info.learncoding.currencyconverter.ui.base.BaseRecyclerAdapter
import javax.inject.Inject

class CurrencyConverterRecyclerAdapter @Inject constructor(
) : BaseRecyclerAdapter<Conversion, RowItemCurrencyResultBinding>() {

    override fun initializeViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): RowItemCurrencyResultBinding {
        return RowItemCurrencyResultBinding.inflate(layoutInflater, parent, false)
    }

    override fun initializeDiffItemCallback(): DiffUtil.ItemCallback<Conversion> {
        return object : DiffUtil.ItemCallback<Conversion>() {
            override fun areItemsTheSame(
                oldItem: Conversion,
                newItem: Conversion
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Conversion,
                newItem: Conversion
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