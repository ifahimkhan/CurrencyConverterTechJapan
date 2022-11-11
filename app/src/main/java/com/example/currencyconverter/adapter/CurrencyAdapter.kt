package com.example.currencyconverter.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.currencyconverter.databinding.ItemHolderBinding
import com.example.currencyconverter.model.RatesModel
import com.example.currencyconverter.util.CurrencyConverter

class CurrencyAdapter constructor(
    private val mList: List<RatesModel>,
    private val currencyConverter: CurrencyConverter = CurrencyConverter(),
    private var amount: String = "1",//Amount 1 is USD
    private var selectedRate: String = "1",//Conversion rate into USD
    private var country: String = "USD"

) : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {


    class ViewHolder(private val itemHolderBinding: ItemHolderBinding) :
        RecyclerView.ViewHolder(itemHolderBinding.root) {
        fun bind(
            model: RatesModel,
            currencyValue: String?,
            currencyConverter: CurrencyConverter,
            amount: String,
            selectedRate: String,
            country: String
        ) {
            itemHolderBinding.title.text = model.currency + " - " + model.countryName

            if (currencyValue == null)
                itemHolderBinding.convertedAmount.text = "N/A"
            else
                itemHolderBinding.convertedAmount.text =
                    "$amount $country = " + currencyConverter.convertCurrency(
                        amount = amount,
                        selectedRate,
                        currencyValue!!
                    )+" ${model.currency}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            ItemHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = mList[position]
        Log.e("TAG", "onBindViewHolder: ${model.currency} ${model.amount}")
        holder.bind(
            model,
            model.amount.toString(),
            currencyConverter,
            amount,
            selectedRate, country
        )
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setAmount(amount: String) {
        this.amount = amount
        notifyDataSetChanged();
    }

    fun setSelectedRate(selectedRate: String, country: String) {
        this.selectedRate = selectedRate
        this.country = country
        notifyDataSetChanged();
    }
}