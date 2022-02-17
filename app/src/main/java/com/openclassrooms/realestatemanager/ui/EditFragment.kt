package com.openclassrooms.realestatemanager.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.FragmentEditBinding
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.Listing
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditFragment : Fragment() {

    // TODO: Utiliser Place Autocomplete Widget pour ajouter l'adresse et récupérer les coordonnées

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var listing: Listing
    private var latestTmpUri: Uri? = null
    private val args: EditFragmentArgs by navArgs()

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((activity?.application as RealEstateManagerApplication).repository)
    }

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
    ): View? {
        _binding = FragmentEditBinding.inflate(inflater, container, false)

        // Recycler view for displaying photos
        binding.recyclerViewMedia.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = PhotoAdapter()
        binding.recyclerViewMedia.adapter = adapter

        // Edit an existing listing or create a new one
        if (args.listingId != null) {
            viewModel.getListing(args.listingId!!).observe(viewLifecycleOwner) {
                // TODO viewModel should return an empty listing if Id doesn't exist
                listing = it
                bind()
                viewModel.getPhotos(listing.id).observe(viewLifecycleOwner, adapter::submitList)
            }
        } else {
            listing = Listing(
                id = Calendar.getInstance().timeInMillis.toString(),
                type = "",
                price = 0,
                address = "",
                latLng = LatLng(0.0,0.0),
                sellStatus = false,
                onSaleDate = Calendar.getInstance()
            )
            viewModel.getPhotos(listing.id).observe(viewLifecycleOwner, adapter::submitList)
        }

        // Floating action button to save changes
        binding.fabSave.setOnClickListener { viewModel.insert(listing) }

        // Floating action buttons to add photos
        binding.fabTakePicture.setOnClickListener { takeImage() }
        binding.fabAddPicture.setOnClickListener { selectImageFromGallery() }

        // Watch changes to textEdits
        binding.texteditType.doAfterTextChanged { listing.type = it.toString() }
        binding.texteditRooms.doAfterTextChanged { listing.numberOfRooms = it.toString().toInt() }
        binding.texteditDescription.doAfterTextChanged { listing.description = it.toString() }
        binding.texteditAddress.doAfterTextChanged { listing.address = it.toString() }
        binding.texteditPrice.doAfterTextChanged { listing.price = it.toString().toInt() }
        binding.texteditArea.doAfterTextChanged { listing.area = it.toString().toInt() }
        binding.texteditRooms.doAfterTextChanged { listing.numberOfRooms = it.toString().toInt() }

        // Click on a date
        binding.texteditOnSaleDate.setOnClickListener { buttonSelectDate() }

        return binding.root
    }

    private fun bind() {
        binding.texteditType.setText(listing.type)
        binding.texteditDescription.setText(listing.description)
        binding.texteditAddress.setText(listing.address)
        binding.texteditRooms.setText(listing.numberOfRooms.toString())
        binding.texteditArea.setText(listing.area.toString())
        binding.texteditPrice.setText(listing.price.toString())
        binding.texteditOnSaleDate.setText(
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            ).format(listing.onSaleDate.time)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun buttonSelectDate() {
        val cal = Calendar.getInstance()
        val builder = MaterialDatePicker.Builder.datePicker()
        builder.setSelection(cal.timeInMillis)
        val datePicker = builder.build()
        datePicker.addOnPositiveButtonClickListener {
            cal.timeInMillis = it
            listing.onSaleDate = cal
            binding.texteditOnSaleDate.setText(
                SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                ).format(cal.time)
            )
        }
        activity?.let { datePicker.show(it.supportFragmentManager, "") }
    }

    private fun takeImage() {
        requireActivity().lifecycleScope.launchWhenStarted {
            getFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

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
            id = Calendar.getInstance().timeInMillis.toString(),
            title = "",
            uri = uri,
            listingId = listing.id
        )
        viewModel.insert(newPhoto)
    }
}