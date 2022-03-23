package com.openclassrooms.realestatemanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.utils.LoanCalculator
import kotlinx.coroutines.launch

class LoanCalculatorViewModel : ViewModel() {

    var totalAmount: Double? = null
        set(n) {
            field = n
            calculateMonthly = true
            calculate()
        }
    var monthlyAmount: Double? = null
        set(n) {
            field = n
            calculateMonthly = false
            calculate()
        }
    var annualRate: Double? = null
        set(n) {
            field = n
            calculate()
        }
    var term: Int? = null
        set(n) {
            field = n
            calculate()
        }

    private var calculateMonthly = true

    private val _totalAmountLiveData = MutableLiveData<Double>()
    val totalAmountLiveData: LiveData<Double>
        get() = _totalAmountLiveData

    private val _monthlyAmountLiveData = MutableLiveData<Double>()
    val monthlyAmountLiveData: LiveData<Double>
        get() = _monthlyAmountLiveData

    private val _totalInterestsLiveData = MutableLiveData<Double>()
    val totalInterestLiveData: LiveData<Double>
        get() = _totalInterestsLiveData

    private fun calculate() {
        when {
            calculateMonthly -> calculateMonthlyAmount()
            else -> calculateTotalAmount()
        }
    }

    private fun calculateMonthlyAmount() = viewModelScope.launch {
        if (totalAmount != null && annualRate != null && term != null) {
            val monthlyAmount = LoanCalculator.monthlyAmount(
                totalAmount!!,
                term!!,
                LoanCalculator.monthlyRate(annualRate!! / 100)
            )
            _monthlyAmountLiveData.value = monthlyAmount
            _totalInterestsLiveData.value = LoanCalculator.totalInterests(
                totalAmount!!,
                term!!,
                monthlyAmount
            )
        }
    }

    private fun calculateTotalAmount() = viewModelScope.launch {
        if (monthlyAmount != null && annualRate != null && term != null) {
            val totalAmount = LoanCalculator.totalAmount(
                monthlyAmount!!,
                term!!,
                LoanCalculator.monthlyRate(annualRate!! / 100)
            )
            _totalAmountLiveData.value = totalAmount
            _totalInterestsLiveData.value = LoanCalculator.totalInterests(
                totalAmount,
                term!!,
                monthlyAmount!!
            )
        }
    }

}