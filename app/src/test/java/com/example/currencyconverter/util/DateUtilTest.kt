package com.example.currencyconverter.util

import org.junit.Before
import org.junit.Test
import java.util.*


class DateUtilTest {
    private lateinit var SUT: DateUtil

    @Before
    fun setUp() {
        println(Calendar.getInstance().timeInMillis)
        SUT = DateUtil(Calendar.getInstance().timeInMillis, Calendar.getInstance().timeInMillis)
    }

    @Test
    fun test_dateCompare_returnsFalse() {
        assert(!SUT.dateCompare())
    }

    @Test
    fun test_dateCompare_returnsTrue() {
        SUT = DateUtil(1668144854178, Calendar.getInstance().timeInMillis)
        assert(SUT.dateCompare())
    }

    @Test
    fun test_dateCompare_BorderLineReturnsFalse() {
        SUT = DateUtil(1668144854178, 1668144854178 + (30 * 60 * 1000))
        assert(!SUT.dateCompare())
    }

    @Test
    fun test_dateCompare_BorderLineReturnsTrue() {
        SUT = DateUtil(1668144854178, 1668144854178 + (31 * 60 * 1000))
        assert(SUT.dateCompare())
    }


}