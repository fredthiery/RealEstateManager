package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.databinding.BottomSheetSearchMinmaxBinding
import com.openclassrooms.realestatemanager.utils.MinMax
import java.text.NumberFormat

class BottomSheetSearchMinMax(
    private var criteria: MinMax,
    private var chip: Chip,
    private val unit: Int? = null,
) : BottomSheetBase() {

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

    private fun refreshUI() {
        binding.editMin.setInt(criteria.min)
        binding.editMax.setInt(criteria.max)

        val closeIcon = (criteria.min != null || criteria.max != null)
        chip.isChecked = closeIcon
        chip.isCloseIconVisible = closeIcon

        viewModel.performSearch()
    }
}

private fun Editable?.toInt() = try {
    NumberFormat.getInstance().parse(this.toString())?.toInt()
} catch (e: Exception) {
    null
}

private fun TextInputEditText.setInt(n: Int?) {
    var string = ""
    n?.let { string = NumberFormat.getInstance().format(it) }
    this.setText(string)
    this.setSelection(string.length)
}
