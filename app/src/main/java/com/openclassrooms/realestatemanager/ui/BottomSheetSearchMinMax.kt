package com.openclassrooms.realestatemanager.ui

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.BottomSheetSearchMinmaxBinding
import com.openclassrooms.realestatemanager.models.MinMax
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory
import java.text.NumberFormat

class BottomSheetSearchMinMax(
    private var criteria: MinMax,
    private var chip: Chip,
    private val unit: Int? = null,
) : BottomSheetBase() {

    private val nf: NumberFormat = NumberFormat.getInstance()

    companion object {
        fun newInstance(criteria: MinMax, chip: Chip, unit: Int?) =
            BottomSheetSearchMinMax(criteria, chip, unit)
    }

    private lateinit var binding: BottomSheetSearchMinmaxBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetSearchMinmaxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.setText(criteria.name)
        if (unit != null) {
            binding.layoutMin.suffixText = resources.getText(unit)
            binding.layoutMax.suffixText = resources.getText(unit)
        }

        binding.editMin.doAfterTextChanged {
            val nb = it.toInt()
            if (nb != criteria.min) {
                criteria.min = nb
                refreshUI()
            }
        }

        binding.editMax.doAfterTextChanged {
            val nb = it.toInt()
            if (nb != criteria.max) {
                criteria.max = nb
                refreshUI()
            }
        }

        chip.setOnCloseIconClickListener {
            criteria.min = null
            criteria.max = null
            refreshUI()
        }

        refreshUI()
    }

    private fun Editable?.toInt() = try {
        nf.parse(this.toString())?.toInt()
    } catch (e: Exception) {
        null
    }

    private fun refreshUI() {
        updateEditText(binding.editMin, criteria.getMinString())
        updateEditText(binding.editMax, criteria.getMaxString())

        val closeIcon = (criteria.min != null || criteria.max != null)
        chip.isChecked = closeIcon
        chip.isCloseIconVisible = closeIcon

        viewModel.performSearch()
    }

    private fun updateEditText(edit: TextInputEditText, text: String) {
        edit.setText(text)
        edit.setSelection(text.length)
    }
}