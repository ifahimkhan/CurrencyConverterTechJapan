package com.example.currencyconverter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.currencyconverter.adapter.CurrencyAdapter
import com.example.currencyconverter.databinding.FragmentCurrencyConverterBinding
import com.example.currencyconverter.model.CountryModel
import com.example.currencyconverter.util.CurrencyConverter
import com.example.currencyconverter.util.NetworkUtil
import com.example.currencyconverter.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
@AndroidEntryPoint
class CurrencyConverterFragment : Fragment() {

    private var _binding: FragmentCurrencyConverterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var mAdapter: CurrencyAdapter
    private var countryList: ArrayList<CountryModel> = ArrayList()
    var job: Job? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentCurrencyConverterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("TAG", "onViewCreated: ", )
        mAdapter = CurrencyAdapter(
            viewModel.listOfRates
        )
        viewModel.checkTimeStamp(NetworkUtil.isConnectingToInternet(requireActivity()))
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            setHasFixedSize(true)
            adapter = mAdapter
        }
        binding.selectionDropdown.setOnClickListener {
            val fragment = SpinnerStaffNameFragment()
            fragment.setArguments(countryList, object : SpinnerSelection {
                override fun onSelect(selected: String?) {
                    binding.selectionDropdown.text = selected ?: ""
                    selected?.let {
                        if (selected.isNotBlank() && selected.contains("-")) {
                            viewModel.setSelectedAmount(
                                selected.substring(
                                    0,
                                    selected.indexOf("-")
                                )
                            )
                        }
                    }
                }
            })

            fragment.show(requireActivity().supportFragmentManager, "spinnerFragment")
        }
        binding.targetCurrency.setOnClickListener {
            val fragment = SpinnerStaffNameFragment()
            fragment.setArguments(countryList, object : SpinnerSelection {
                override fun onSelect(selected: String?) {
                    binding.targetCurrency.text = selected ?: ""
                    targetCurrencyConvert()
                }
            })
            fragment.show(requireActivity().supportFragmentManager, "spinnerFragment")
        }
        binding.amount.addTextChangedListener {
            job?.cancel()
            job = lifecycleScope.launch {
                delay(1000)
                it?.let {
                    if (it.toString().isNotBlank()) {
                        viewModel.convertCurrency(it.toString())
                    }
                }
            }
        }

        binding.targetAmount.addTextChangedListener {
            job?.cancel()
            job = lifecycleScope.launch {
                delay(1000)
                it?.let {
                    if (it.toString().isNotBlank()) {
                        convertSourceCurrency()
                    }
                }
            }
        }
        subscribeToObservers()

    }


    private fun subscribeToObservers() {

        viewModel.loadingLiveData.observe(viewLifecycleOwner) {
            it?.let {
                showLoadingDialog(it)
            }
        }
        viewModel.countriesLiveData.observe(viewLifecycleOwner) {
            it?.let {
                countryList.clear()
                it.forEach { key, value -> countryList.add(CountryModel(key, value)) }
                Log.e("TAG", "subscribeToObservers: ${countryList.size}")
            }
        }

        viewModel.ratesLiveData.observe(viewLifecycleOwner) {
            mAdapter.notifyDataSetChanged()
        }
        viewModel.amountLiveData.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotBlank()) {
                    targetCurrencyConvert()
                    mAdapter.setAmount(it)
                }

            }
        }
        viewModel.selectedLiveData.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isNotBlank()) {
                    targetCurrencyConvert()
                    mAdapter.setSelectedRate(it.substring(0,it.indexOf("-")),it.substring(it.indexOf("-")+1))
                }
            }
        }
        viewModel.notConnectingInternetLiveData.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    val builder = AlertDialog.Builder(requireActivity())
                    //set title for alert dialog
                    builder.setTitle(R.string.no_internet_connection)
                    //set message for alert dialog
                    builder.setMessage(R.string.pls_try_again)
                    builder.setIcon(android.R.drawable.ic_dialog_alert)

                    //performing positive action
                    builder.setNeutralButton("OK") { dialogInterface, _ ->
                        binding.targetAmount.isEnabled = false
                        binding.amount.isEnabled = false
                        binding.targetCurrency.isEnabled = false
                        binding.selectionDropdown.isEnabled = false
                        if (NetworkUtil.isConnectingToInternet(requireActivity())){
                            dialogInterface.dismiss()
                            viewModel.checkTimeStamp(
                                NetworkUtil.isConnectingToInternet(
                                    requireActivity()
                                )
                            )
                        }

                    }

                    // Create the AlertDialog
                    val alertDialog: AlertDialog = builder.create()
                    // Set other dialog properties
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Log.e("TAG", "notconnectinginternet: ")
                } else {
                    binding.targetAmount.isEnabled = true
                    binding.amount.isEnabled = true
                    binding.targetCurrency.isEnabled = true
                    binding.selectionDropdown.isEnabled = true

                }
            }

        }
        viewModel.noDataFoundLiveData.observe(viewLifecycleOwner) {
            it?.let {
                if (it) {
                    val builder = AlertDialog.Builder(requireActivity())
                    //set title for alert dialog
                    builder.setTitle(getString(R.string.no_data_found))
                    //set message for alert dialog
                    builder.setMessage(getString(R.string.free_apis))
                    builder.setIcon(android.R.drawable.ic_dialog_alert)

                    //performing positive action
                    builder.setNeutralButton("OK") { dialogInterface, _ ->
                        binding.targetAmount.isEnabled = false
                        binding.amount.isEnabled = false
                        binding.targetCurrency.isEnabled = false
                        binding.selectionDropdown.isEnabled = false
//                        viewModel.checkTimeStamp(NetworkUtil.isConnectingToInternet(requireActivity()))

                    }

                    // Create the AlertDialog
                    val alertDialog: AlertDialog = builder.create()
                    // Set other dialog properties
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                } else {
                    binding.targetAmount.isEnabled = true
                    binding.amount.isEnabled = true
                    binding.targetCurrency.isEnabled = true
                    binding.selectionDropdown.isEnabled = true

                }
            }
        }

    }

    private fun convertSourceCurrency() {
        if (binding.targetAmount.text.toString().isEmpty()) return
        val currencyConverter = CurrencyConverter()

        val amount = binding.targetAmount.text.toString().toDouble()
        var currency = binding.targetCurrency.text.toString()
        currency = currency.substring(0, currency.indexOf("-"))
        val rate = viewModel.dataModel?.rates?.get(currency)

        var targetcurrency = binding.selectionDropdown.text.toString()
        targetcurrency = targetcurrency.substring(0, targetcurrency.indexOf("-"))
        val targetRate = viewModel.dataModel?.rates?.get(targetcurrency)


        binding.amount.setText(
            currencyConverter.convertCurrency(
                amount = amount.toString(),
                selectedCurrencyRate = rate.toString(),
                targetRate.toString()
            )
        )

    }

    private fun targetCurrencyConvert() {
        if (binding.amount.text.toString().isEmpty()) return

        val currencyConverter = CurrencyConverter()

        val amount = binding.amount.text.toString().toDouble()
        var currency = binding.selectionDropdown.text.toString()
        currency = currency.substring(0, currency.indexOf("-"))
        val rate = viewModel.dataModel?.rates?.get(currency)

        var targetcurrency = binding.targetCurrency.text.toString()
        targetcurrency = targetcurrency.substring(0, targetcurrency.indexOf("-"))
        val targetRate = viewModel.dataModel?.rates?.get(targetcurrency)


        binding.targetAmount.setText(
            currencyConverter.convertCurrency(
                amount = amount.toString(),
                selectedCurrencyRate = rate.toString(),
                targetRate.toString()
            )
        )
        if (binding.targetAmount.isFocusable) {
            binding.targetAmount.setSelection(binding.targetAmount.text.length)
        }
        job?.cancel()


    }

    private fun showLoadingDialog(showLoading: Boolean) {
        when (showLoading) {
            true -> binding.progressbar.visibility = View.VISIBLE
            false -> binding.progressbar.visibility = View.GONE
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface SpinnerSelection {
        fun onSelect(selected: String?)
    }
}