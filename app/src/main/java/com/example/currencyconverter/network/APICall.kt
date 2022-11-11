package com.example.currencyconverter.network

import retrofit2.http.GET
import com.example.example.CurrenciesModel
import com.example.example.LatestDataModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Query
import java.util.*

interface APICall {
    @GET("/api/currencies.json")
    suspend fun getCurrencies(): Response<TreeMap<String, String>>?

    @GET("/api/latest.json")
    suspend fun getLatestData(@Query("app_id") app_id: String): Response<LatestDataModel?>?
}