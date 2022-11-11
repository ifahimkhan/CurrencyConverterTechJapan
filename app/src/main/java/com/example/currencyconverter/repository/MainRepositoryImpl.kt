package com.example.currencyconverter.repository

import android.util.Log
import com.example.currencyconverter.network.APICall
import com.example.currencyconverter.util.Constant
import com.example.example.CurrenciesModel
import com.example.example.LatestDataModel
import com.google.gson.Gson
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class MainRepositoryImpl @Inject constructor(private val apiCall: APICall) : MainRepository {
    override suspend fun getCountries(): TreeMap<String, String>? {
        val response = apiCall.getCurrencies()
        Log.e("TAG", "${Gson().toJson(response?.body())} getCountries: ")
        return response?.let {
            return@let response.body()
        }
    }

    override suspend fun getLatestData(): LatestDataModel? {
        val response = apiCall.getLatestData(Constant.APP_ID)
        return response?.let {
            return@let response.body()
        }
    }
}