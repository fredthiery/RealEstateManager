package com.openclassrooms.realestatemanager.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.BottomSheetAddPhotoBinding
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory
import java.io.File
import java.util.*

class BottomSheetAddPhoto : BottomSheetBase() {

    companion object {
        fun newInstance() = BottomSheetAddPhoto()
    }

    private lateinit var binding: BottomSheetAddPhotoBinding
    private var latestTmpUri: Uri? = null

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) latestTmpUri?.let(this::insertPhoto)
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let(this::insertPhoto)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.actionAddFromGalery.setOnClickListener {
            selectImageFromGalleryResult.launch("image/*")
        }

        binding.actionAddFromCamera.setOnClickListener {
            requireActivity().lifecycleScope.launchWhenStarted {
                getFileUri().let { uri ->
                    latestTmpUri = uri
                    takeImageResult.launch(uri)
                }
            }
        }
    }

    private fun getFileUri(): Uri {
        val file = File(requireActivity().filesDir, "${UUID.randomUUID()}.jpg")

        return FileProvider.getUriForFile(
            requireContext(),
            "${BuildConfig.APPLICATION_ID}.provider",
            file
        )
    }

    private fun insertPhoto(uri: Uri) {
        val newPhoto = Photo(
            title = "",
            uri = uri,
            listingId = viewModel.editListing.listing.id
        )
        viewModel.add(newPhoto)
    }
}