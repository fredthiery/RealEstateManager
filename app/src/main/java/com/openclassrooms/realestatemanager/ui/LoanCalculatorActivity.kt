package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityLoanCalculatorBinding
import com.openclassrooms.realestatemanager.viewmodels.LoanCalculatorViewModel
import java.text.NumberFormat
import kotlin.math.roundToInt

class LoanCalculatorActivity : AppCompatActivity() {

    private lateinit var viewModel: LoanCalculatorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLoanCalculatorBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[LoanCalculatorViewModel::class.java]

        val currency = PreferenceManager
            .getDefaultSharedPreferences(this)
            .getString("currency", "dollar")

        when (currency) {
            "euro" -> {
                binding.layoutAmount.prefixText = ""
                binding.layoutAmount.suffixText = "€"
                binding.texteditAmount.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
                binding.layoutMonthlyAmount.prefixText = ""
                binding.layoutMonthlyAmount.suffixText = "€"
                binding.texteditMonthlyAmount.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
            }
            else -> {
                binding.layoutAmount.prefixText = "$"
                binding.layoutAmount.suffixText = ""
                binding.texteditAmount.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                binding.layoutMonthlyAmount.prefixText = "$"
                binding.layoutMonthlyAmount.suffixText = ""
                binding.texteditMonthlyAmount.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            }
        }

        binding.texteditAmount.doAfterTextChanged {
            val view = binding.texteditAmount
            if (view.tag == null) {
                val n = it.toDouble()
                view.setDouble(n)
                viewModel.totalAmount = n
            }
        }
        binding.texteditMonthlyAmount.doAfterTextChanged {
            val view = binding.texteditMonthlyAmount
            if (view.tag == null) {
                val n = it.toDouble()
                view.setDouble(n)
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

        viewModel.totalAmountLiveData.observe(this) {
            binding.texteditAmount.setInt(it.roundToInt())
        }

        viewModel.monthlyAmountLiveData.observe(this) {
            binding.texteditMonthlyAmount.setDouble(it)
        }

        viewModel.totalInterestLiveData.observe(this) {
            binding.result.text = String.format(
                resources.getString(R.string.interests_result),
                NumberFormat.getInstance().format(it.roundToInt()),
                when (currency) {
                    "euro" -> "€"
                    else -> "$"
                }
            )
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
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
        setDouble(n?.toDouble())
    }

    private fun TextInputEditText.setDouble(n: Double?) {
        val sel = (text?.length ?: 0) - selectionEnd

        val nFormat = NumberFormat.getInstance()
        nFormat.maximumFractionDigits = 2

        var string = ""
        n?.let {
            string = nFormat.format(it)
        }

        tag = "User Input Disabled"
        setText(string)
        tag = null

        setSelection(string.length - sel)
    }
}