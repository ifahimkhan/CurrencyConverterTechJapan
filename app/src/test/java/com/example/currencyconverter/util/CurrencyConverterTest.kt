package com.example.currencyconverter.util

import org.junit.Test


internal class CurrencyConverterTest {

    val currencyConverter = CurrencyConverter()

    //    Working Test case
    @Test
    fun testConversionINRTOAED_SHOULD_RETURNS_EXPECTEDVALUE() {
        val convertedAmount = currencyConverter.convertCurrency("163", "81.9870", "3.6745")
        println(convertedAmount)
        val expectedAmount = currencyConverter.df.format("7.305".toDouble())
        assert(expectedAmount.equals(convertedAmount))
    }

    //    If Empty Field is pass to the function
    @Test
    fun test_ConversionINR_To_AED_IF_SELECTEDRATE_IS_EMPTY_ReturnsNA() {
        val convertedAmount = currencyConverter.convertCurrency("163", "", "3.6745")
        println(convertedAmount)
        val expectedAmount = "N/A"
        assert(expectedAmount == convertedAmount)
    }

    @Test
    fun test_ConversionINR_To_AED_IF_AMOUNT_IS_EMPTYReturnsNA() {
        val convertedAmount = currencyConverter.convertCurrency("", "81.9870", "3.6745")
        println(convertedAmount)
        val expectedAmount = "N/A"
        assert(expectedAmount == convertedAmount)
    }

    @Test
    fun test_ConversionINR_To_AED_IF_TARGETRATE_IS_EMPTYReturnsNA() {
        val convertedAmount = currencyConverter.convertCurrency("163", "81.9870", "")
        println(convertedAmount)
        val expectedAmount = "N/A"
        assert(expectedAmount == convertedAmount)
    }
}