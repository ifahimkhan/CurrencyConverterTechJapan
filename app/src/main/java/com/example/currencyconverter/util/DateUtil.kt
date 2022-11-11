package com.example.currencyconverter.util

import android.util.Log
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class DateUtil(private val lastDate: Long, private val currentDate: Long) {


    //    adding 30 min in the last date to compare with current time and refresh the data
    fun dateCompare(): Boolean {
        val currentDate = Date(Timestamp(currentDate).time);
        val lastDate = Date(Timestamp(lastDate).time + (30 * 60 * 1000))
        println("lastdate after adding 30min: $lastDate")
        println("current date: $currentDate")
//        Log.e("TAG", "dateCompare: ${lastDate.compareTo(currentDate)}")
        return lastDate.compareTo(currentDate) < 0
    }

}