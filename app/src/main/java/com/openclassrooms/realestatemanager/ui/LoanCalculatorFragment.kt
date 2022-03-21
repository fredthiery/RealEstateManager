package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentLoanCalculatorBinding
import com.openclassrooms.realestatemanager.viewmodels.LoanCalculatorViewModel
import java.text.NumberFormat
import kotlin.math.roundToInt

class LoanCalculatorFragment : Fragment() {

    companion object {
        fun newInstance() = LoanCalculatorFragment()
    }

    private lateinit var viewModel: LoanCalculatorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoanCalculatorBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[LoanCalculatorViewModel::class.java]

        binding.texteditAmount.doAfterTextChanged {
            val view = binding.texteditAmount
            if (view.tag == null) {
                val n = it.toDouble()
                view.setDouble(n, 0)
                viewModel.totalAmount = n
            }
        }
        binding.texteditMonthlyAmount.doAfterTextChanged {
            val view = binding.texteditMonthlyAmount
            if (view.tag == null) {
                val n = it.toDouble()
                view.setDouble(n, 0)
                viewModel.monthlyAmount = n
            }
        }
        binding.texteditRate.doAfterTextChanged {
            val view = binding.texteditRate
            if (view.tag == null) {
                val n = it.toDouble()
                viewModel.annualRate = n
            }
        }
        binding.texteditTerm.doAfterTextChanged {
            val view = binding.texteditTerm
            if (view.tag == null) {
                val n = it.toInt()
                view.setInt(n)
                viewModel.term = n
            }
        }

        viewModel.totalAmountLiveData.observe(viewLifecycleOwner) {
            binding.texteditAmount.setDouble(it, 0)
        }

        viewModel.monthlyAmountLiveData.observe(viewLifecycleOwner) {
            binding.texteditMonthlyAmount.setDouble(it, 0)
        }

        viewModel.totalInterestLiveData.observe(viewLifecycleOwner) {
            binding.result.text = String.format(resources.getString(R.string.interests_result), it.roundToInt())
        }

        return binding.root
    }

}

private fun Editable?.toInt() = try {
    NumberFormat.getInstance().parse(this.toString())?.toInt()
} catch (e: Exception) {
    null
}

private fun Editable?.toDouble() = try {
    NumberFormat.getInstance().parse(this.toString())?.toDouble()
} catch (e: Exception) {
    null
}

private fun TextInputEditText.setInt(n: Int?) {
    setDouble(n?.toDouble(), 0)
}

private fun TextInputEditText.setDouble(n: Double?, precision: Int = 2) {
    val sel = (text?.length ?: 0) - selectionEnd

    val nFormat = NumberFormat.getInstance()
    nFormat.maximumFractionDigits = precision

    var string = ""
    n?.let {
        string = nFormat.format(it)
    }

    tag = "User Input Disabled"
    setText(string)
    tag = null

    setSelection(string.length - sel)
}