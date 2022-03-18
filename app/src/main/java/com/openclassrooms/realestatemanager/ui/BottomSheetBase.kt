package com.openclassrooms.realestatemanager.ui

import android.app.Dialog
import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory

abstract class BottomSheetBase: BottomSheetDialogFragment() {

    val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((activity?.application as RealEstateManagerApplication).repository)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheet: FrameLayout = dialog.findViewById(R.id.design_bottom_sheet)

            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.skipCollapsed = true
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
        return dialog
    }
}