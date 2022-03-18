package com.openclassrooms.realestatemanager

import com.google.common.truth.Truth.assertThat
import com.openclassrooms.realestatemanager.models.Converters
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class ConvertersTest {

    private val classUnderTest = Converters()

    @Test
    fun toAndFromTimestampTest() {
        val testCal = Calendar.getInstance()
        val result = classUnderTest.fromTimestamp(classUnderTest.toTimestamp(testCal))
        assertThat(testCal).isEqualTo(result)
    }

    @Test
    fun toAndFromUri() {
        val result = classUnderTest.fromUri(classUnderTest.toUri("test"))
        assertThat(result).isEqualTo("test")
    }

    @Test
    fun toAndFromLatLng() {
        val result = classUnderTest.fromLatLng(classUnderTest.toLatLng("0.123, 0.456"))
        assertThat(result).isEqualTo("0.123,0.456")
    }
}