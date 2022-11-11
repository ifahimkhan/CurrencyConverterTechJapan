package com.example.currencyconverter.repository

import com.example.example.CurrenciesModel
import com.example.example.LatestDataModel
import java.util.*

interface MainRepository {
    suspend fun getCountries(): TreeMap<String, String>?
    suspend fun getLatestData(): LatestDataModel?

}