package com.openclassrooms.realestatemanager

import com.google.common.truth.Truth.assertThat
import com.openclassrooms.realestatemanager.utils.LoanCalculator
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LoanCalculatorTest {
    @Test
    fun monthlyPaymentTest() {
        var monthlyPayment = LoanCalculator.monthlyAmount(100000.0, 240, 0.005)
        assertThat(monthlyPayment).isWithin(0.1).of(716.43)

        monthlyPayment = LoanCalculator.monthlyAmount(150000.0, 240, LoanCalculator.monthlyRate(0.0234))
        assertThat(monthlyPayment).isWithin(0.1).of(781.48)
    }

    @Test
    fun totalAmountTest() {
        var totalAmount = LoanCalculator.totalAmount(716.43,240,0.005)
        assertThat(totalAmount).isWithin(1.0).of(100000.0)
    }

    @Test
    fun totalInterestsTest() {
        var totalInterests = LoanCalculator.totalInterests(100000.0, 240, 716.43)
        assertThat(totalInterests).isWithin(0.1).of(71943.2)

        totalInterests = LoanCalculator.totalInterests(150000.0,240,781.48)
        assertThat(totalInterests).isWithin(1.0).of(37554.53)
    }

    @Test
    fun monthlyRateTest() {
        val monthlyRate = LoanCalculator.monthlyRate(0.04)
        assertThat(monthlyRate).isWithin(1.0e-5).of(0.00327)
    }

}