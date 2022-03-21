package com.openclassrooms.realestatemanager.utils

import kotlin.math.pow

class LoanCalculator {

    companion object {
        fun monthlyAmount(
            loanAmount: Double,
            termInMonths: Int,
            monthlyRate: Double
        ): Double {
            return (loanAmount * monthlyRate * (1 + monthlyRate).pow(termInMonths)) /
                    ((1 + monthlyRate).pow(termInMonths) - 1)
        }

        fun totalAmount(
            monthlyPayment: Double,
            termInMonths: Int,
            monthlyRate: Double
        ): Double {
            return (monthlyPayment * ((1 + monthlyRate).pow(termInMonths) - 1)) /
                    (monthlyRate * (1 + monthlyRate).pow(termInMonths))
        }

        fun totalInterests(loanAmount: Double, termInMonths: Int, monthlyPayment: Double): Double {
            return (monthlyPayment * termInMonths) - loanAmount
        }

        fun monthlyRate(interestRate: Double): Double {
            return (1 + interestRate).pow(1.0 / 12) - 1
        }
    }
}