package com.example.currencyconverter.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.example.gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.*
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

private const val TIME_STAMP_KEY = "TIME_STAMP_KEY"


class UserPreferenceRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val TIME_STAMP_KEY = longPreferencesKey("TIME_STAMP_KEY")
        val RATES = stringPreferencesKey("RATES")
        val COUNTRIES = stringPreferencesKey("COUNTRIES")
    }

    data class UserPreferences(val timestamp: Long)
    data class UserPreferencesRates(
        val rates: TreeMap<String, String>,
        val countries: TreeMap<String, String>
    )

    val timestampFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val timestamp = preferences[PreferencesKeys.TIME_STAMP_KEY] ?: 0L
            UserPreferences(timestamp)
        }

    suspend fun updateTimeStamp(timestamp: Long) {
        Log.e("TAG", "updateTimeStamp: $timestamp")

        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TIME_STAMP_KEY] = timestamp
        }
    }

    suspend fun updateRates(rates: TreeMap<String, String>) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.RATES] = gson.toJson(rates)
        }
    }

    val ratesFlow: Flow<UserPreferencesRates> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val json = preferences[PreferencesKeys.RATES] ?: ""
            var map: TreeMap<String, String> = TreeMap()
            if (json.isNotBlank())
                map = gson.fromJson(
                    json,
                    object : TypeToken<TreeMap<String, String>>() {}.rawType
                ) as TreeMap<String, String>


            val countryJson = preferences[PreferencesKeys.COUNTRIES] ?: ""
            var countryMap: TreeMap<String, String> = TreeMap()
            if (countryJson.isNotBlank())
                countryMap =
                    gson.fromJson(
                        countryJson,
                        object : TypeToken<TreeMap<String, String>>() {}.rawType
                    ) as TreeMap<String, String>

            UserPreferencesRates(map, countryMap)
        }

    suspend fun updateCountries(countries: TreeMap<String, String>) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.COUNTRIES] = gson.toJson(countries)
        }
    }
}