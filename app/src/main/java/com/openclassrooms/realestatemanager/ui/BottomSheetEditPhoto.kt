package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.BottomSheetEditPhotoBinding
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory

class BottomSheetEditPhoto(var photo: Photo) : BottomSheetBase() {

    companion object {
        fun newInstance(photo: Photo) = BottomSheetEditPhoto(photo)
    }

    private lateinit var binding: BottomSheetEditPhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetEditPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.texteditTitle.setText(photo.title)

        binding.texteditTitle.doAfterTextChanged {
            photo.title = it.toString()
            viewModel.update(photo)
        }

        binding.actionSelectThumbnail.setOnClickListener {
            viewModel.setThumbnail(photo.id)
        }

        binding.actionDelete.setOnClickListener {
            viewModel.delete(photo)
        }
    }
}