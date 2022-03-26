package com.openclassrooms.realestatemanager

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.openclassrooms.realestatemanager.utils.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UtilsInstrumentedTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @Test
    fun internetAvailabilityTest() = runBlocking {
        instrumentation.uiAutomation.executeShellCommand("svc wifi disable")
        instrumentation.uiAutomation.executeShellCommand("svc data disable")
        delay(500)
        assertThat(Utils.isInternetAvailable(instrumentation.context)).isFalse()

        instrumentation.uiAutomation.executeShellCommand("svc wifi enable")
        instrumentation.uiAutomation.executeShellCommand("svc data enable")
        delay(500)
        assertThat(Utils.isInternetAvailable(instrumentation.context)).isTrue()
    }
}