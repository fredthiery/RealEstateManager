package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.BottomSheetSearchPoisBinding
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory

class BottomSheetSearchPOIs(
    private var parentChip: Chip,
) : BottomSheetDialogFragment() {

    private var nChecked = 0
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((activity?.application as RealEstateManagerApplication).repository)
    }

    companion object {
        fun newInstance(chip: Chip) =
            BottomSheetSearchPOIs(chip)
    }

    private lateinit var binding: BottomSheetSearchPoisBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetSearchPoisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val types = resources.getStringArray(R.array.poi_types)
        for (type in types) {
            val index = types.indexOf(type)
            val chip = Chip(context)
            chip.text = type
            chip.isCheckable = true

            viewModel.searchCriteria.pointsOfInterest[index]?.let {
                chip.isChecked = it
                nChecked += if (it) 1 else 0
            }

            chip.setOnCheckedChangeListener { _, state ->
                viewModel.searchCriteria.pointsOfInterest[index] = state
                nChecked += if (state) 1 else -1
                refreshUI()
            }
            binding.chipGroup.addView(chip)
        }

        parentChip.setOnCloseIconClickListener {
            viewModel.searchCriteria.pointsOfInterest.clear()
            nChecked = 0
            refreshUI()
        }
    }

    private fun refreshUI() {
        parentChip.isCloseIconVisible = nChecked != 0
        viewModel.performSearch()
    }
}