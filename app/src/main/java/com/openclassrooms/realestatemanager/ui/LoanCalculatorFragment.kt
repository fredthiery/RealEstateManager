package com.openclassrooms.realestatemanager.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.viewmodels.LoanCalculatorViewModel

class LoanCalculatorFragment : Fragment() {

    companion object {
        fun newInstance() = LoanCalculatorFragment()
    }

    private lateinit var viewModel: LoanCalculatorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.loan_calculator_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[LoanCalculatorViewModel::class.java]
        // TODO: Use the ViewModel
    }

}