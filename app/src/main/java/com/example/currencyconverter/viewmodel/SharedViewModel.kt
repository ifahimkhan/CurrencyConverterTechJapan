package com.example.currencyconverter.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.model.RatesModel
import com.example.currencyconverter.repository.MainRepository
import com.example.currencyconverter.repository.UserPreferenceRepository
import com.example.currencyconverter.util.DateUtil
import com.example.currencyconverter.util.NetworkUtil
import com.example.example.LatestDataModel
import com.example.example.Rates
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) :
    ViewModel() {

    private val _rates: MutableLiveData<List<RatesModel>> = MutableLiveData()
    val ratesLiveData: LiveData<List<RatesModel>> = _rates
    val listOfRates: ArrayList<RatesModel> = object : ArrayList<RatesModel>() {}

    private val _countries: MutableLiveData<TreeMap<String, String>> = MutableLiveData()
    val countriesLiveData: LiveData<TreeMap<String, String>> = _countries
    var countries: TreeMap<String, String> = TreeMap()

    private val timeStampFlow = userPreferenceRepository.timestampFlow
    var dataModel: LatestDataModel? = null

    private var _loading: MutableLiveData<Boolean> = MutableLiveData<Boolean>(null)
    val loadingLiveData = _loading


    private var _notConnectingInternet: MutableLiveData<Boolean> = MutableLiveData<Boolean>(null)
    val notConnectingInternetLiveData = _notConnectingInternet


    private var _noDataFoundViaAPI: MutableLiveData<Boolean> = MutableLiveData<Boolean>(null)
    val noDataFoundLiveData = _noDataFoundViaAPI


    fun callCurrencies() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            countries = mainRepository.getCountries()!!
            _loading.postValue(false)
        }
    }

    private fun callLatestData(connectingToInternet: Boolean, callApi: Boolean) {
        parentJob?.cancel()
        parentJob = viewModelScope.launch(Dispatchers.IO) {
            Log.e("TAG", "connectingToInternet:  $connectingToInternet callapi: $callApi")
            _loading.postValue(true)
            if (callApi && connectingToInternet) {
                countries = mainRepository.getCountries() ?: TreeMap()
                _countries.postValue(countries)
                dataModel = mainRepository.getLatestData()
                countries.let { userPreferenceRepository.updateCountries(countries) }
                dataLoaded(connectingToInternet)
                delay(10000)
                dataModel?.let {
                    userPreferenceRepository.updateRates(it.rates)
                    userPreferenceRepository.updateTimeStamp(Calendar.getInstance().timeInMillis)
                }


            } else {
                userPreferenceRepository.ratesFlow.collect { storedRates ->
                    countries.clear();
                    countries.putAll(storedRates.countries)
                    _countries.postValue(countries)
                    dataModel = LatestDataModel(rates = storedRates.rates)
                    Log.e("TAG", "callLatestData: ${dataModel!!.rates.size}")
                    dataLoaded(connectingToInternet)
                }
            }


        }
    }

    private fun dataLoaded(connectingToInternet: Boolean) {
        _loading.postValue(false)
        listOfRates.clear()
        dataModel?.rates?.map { addRate(it.key, countries.get(key = it.key) ?: "", it.value) }
        _rates.postValue(listOfRates)
        Log.e("TAG", "finished: ")
        if (countries.size == 0 || dataModel?.rates?.size == 0) {

            if (!connectingToInternet) {
                _notConnectingInternet.postValue(true)
            } else {
                _noDataFoundViaAPI.postValue(true)

            }
        } else {
            _noDataFoundViaAPI.postValue(false)
            _notConnectingInternet.postValue(false)
        }


    }

    private fun addRate(currency: String, Country: String, amount: String) {

        listOfRates.add(
            RatesModel(
                currency = currency,
                countryName = Country,
                amount = amount.toDouble()
            )
        )

    }

    var parentJob: Job? = null
    fun checkTimeStamp(connectingToInternet: Boolean) {
        viewModelScope.launch {
            timeStampFlow.collect() {
                val dateUtil = DateUtil(it.timestamp, Calendar.getInstance().timeInMillis)
                callLatestData(connectingToInternet, dateUtil.dateCompare())
                return@collect

            }
        }

    }

    private val _amountLiveData: MutableLiveData<String> = MutableLiveData("")
    val amountLiveData: LiveData<String> = _amountLiveData

    fun convertCurrency(amount: String) {
        _amountLiveData.value = amount

    }

    private val _selectedLiveData: MutableLiveData<String> = MutableLiveData("")
    val selectedLiveData: LiveData<String> = _selectedLiveData

    fun setSelectedAmount(selected: String) {
        Log.e("TAG", "setSelectedAmount:$selected ")
        if (dataModel != null && dataModel?.rates != null)
            _selectedLiveData.value = dataModel?.rates!![selected] +"-"+selected
    }


}