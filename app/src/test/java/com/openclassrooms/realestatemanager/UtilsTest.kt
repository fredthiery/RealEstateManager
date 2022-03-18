package com.openclassrooms.realestatemanager

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.text.SimpleDateFormat
import java.util.*

@RunWith(JUnit4::class)
class UtilsTest {

    @Test
    fun convertDollarToEuroTest() {
        val result = Utils.convertDollarToEuro(500)
        assertThat(result).isEqualTo(406)
    }

    @Test
    fun convertEuroToDollarTest() {
        val result = Utils.convertEuroToDollar(500)
        assertThat(result).isEqualTo(616)
    }

    @Test
    fun getTodayDateTest() {
        val date = SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().time)

        val result = Utils.getTodayDate()

        assertThat(result).isEqualTo(date)
    }
}