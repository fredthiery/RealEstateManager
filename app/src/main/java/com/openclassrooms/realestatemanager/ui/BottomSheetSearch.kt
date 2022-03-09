package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.BottomSheetSearchMinmaxBinding
import com.openclassrooms.realestatemanager.models.MinMax
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory
import java.text.NumberFormat

class BottomSheetSearch(
    private var criteria: MinMax,
    private var chip: Chip,
    private val unit: Int? = null,
) : BottomSheetDialogFragment() {

    private val nf: NumberFormat = NumberFormat.getInstance()
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((activity?.application as RealEstateManagerApplication).repository)
    }

    companion object {
        fun newInstance(criteria: MinMax, chip: Chip, unit: Int?) =
            BottomSheetSearch(criteria, chip, unit)
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
            val nb: Int = try {
                nf.parse(it.toString())?.toInt() ?: 0
            } catch (e: Exception) {
                0
            }
            if (nb != criteria.min) {
                criteria.min = nb
                refresh()
            }
        }

        binding.editMax.doAfterTextChanged {
            val nb = try {
                nf.parse(it.toString())?.toInt() ?: Int.MAX_VALUE
            } catch (e: Exception) {
                Int.MAX_VALUE
            }
            if (nb != criteria.max) {
                criteria.max = nb
                refresh()
            }
        }

        chip.setOnCloseIconClickListener {
            criteria.min = 0
            criteria.max = Int.MAX_VALUE
            refresh()
        }

        refresh()
    }

    private fun refresh() {
        updateEditText(binding.editMin, criteria.getMinString())
        updateEditText(binding.editMax, criteria.getMaxString())

        if (criteria.min != 0 || criteria.max != Int.MAX_VALUE) {
            chip.isChecked = true
            chip.isCloseIconVisible = true
            viewModel.performSearch()
        } else {
            chip.isChecked = false
            chip.isCloseIconVisible = false
        }
    }

    private fun updateEditText(edit: TextInputEditText, text: String) {
        edit.setText(text)
        edit.setSelection(text.length)
    }
}