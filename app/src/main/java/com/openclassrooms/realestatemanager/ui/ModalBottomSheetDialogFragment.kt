package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.openclassrooms.realestatemanager.databinding.BottomSheetEditPhotoBinding
import com.openclassrooms.realestatemanager.models.Photo

class ModalBottomSheetDialogFragment(var photo: Photo) : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(photo: Photo) = ModalBottomSheetDialogFragment(photo)
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
    }
}