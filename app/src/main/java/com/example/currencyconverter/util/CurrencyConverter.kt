package com.example.currencyconverter.util

import android.util.Log
import com.example.example.gson
import java.text.DecimalFormat
import javax.inject.Inject

class CurrencyConverter @Inject constructor() {
    var df = DecimalFormat("#.###")

    fun convertCurrency(
        amount: String,
        selectedCurrencyRate: String,
        CurrencyRate: String
    ): String {
        if (amount.isEmpty()) return "N/A"
        if (selectedCurrencyRate.isEmpty()) return "N/A"
        if (CurrencyRate.isEmpty()) return "N/A"

        var convertedAmount: Double = 0.0
        convertedAmount = CurrencyRate.toDouble() * convertToUSD(amount, selectedCurrencyRate)
        return df.format(convertedAmount)
    }

    private fun convertToUSD(amount: String, selectedCurrencyRate: String): Double {
//        Log.e("TAG", "convertToUSD: $amount $selectedCurrencyRate")
        return amount.toDouble() / selectedCurrencyRate.toDouble()
    }

}