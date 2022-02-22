package com.openclassrooms.realestatemanager.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.FragmentEditBinding
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.Listing
import com.openclassrooms.realestatemanager.models.geoCodingResponse.Place
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory
import java.io.File
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*

class EditFragment : Fragment() {

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
        val adapter = PhotoAdapter {
            listing.thumbnail = it.uri
            ModalBottomSheetDialogFragment.newInstance(it).show(childFragmentManager,ModalBottomSheetDialogFragment::class.java.canonicalName)
            // TODO listing devrait être dans le viewModel plutôt qu'ici
        }
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
                id = UUID.randomUUID().toString(),
                type = "",
                price = 0,
                address = "",
                latLng = LatLng(0.0,0.0),
                sellStatus = false,
                onSaleDate = Calendar.getInstance()
            )
            viewModel.getPhotos(listing.id).observe(viewLifecycleOwner, adapter::submitList)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Floating action button to save changes
        binding.fabSave.setOnClickListener { viewModel.insert(listing) }

        // Floating action buttons to add photos
        binding.fabTakePicture.setOnClickListener { takeImage() }
        binding.fabAddPicture.setOnClickListener { selectImageFromGallery() }

        // Watch changes to textEdits
        binding.texteditType.doAfterTextChanged { listing.type = it.toString() }
        binding.texteditRooms.doAfterTextChanged { listing.numberOfRooms = it.toString().toInt() }
        binding.texteditDescription.doAfterTextChanged { listing.description = it.toString() }
        binding.texteditPrice.doAfterTextChanged { listing.price = it.toString().toInt() }
        binding.texteditArea.doAfterTextChanged { listing.area = it.toString().toInt() }
        binding.texteditRooms.doAfterTextChanged { listing.numberOfRooms = it.toString().toInt() }
        binding.texteditAddress.doAfterTextChanged { validateAddress(it.toString()) }

        // Click on a date
        binding.texteditOnSaleDate.setOnClickListener { buttonSelectDate() }
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

    private fun validateAddress(address: String) {
        listing.address = address
        if (address.length > 10) {
            viewModel.getLocation(address).observe(viewLifecycleOwner) { locations ->
                // Populate the AutoComplete dropdown
                val locationStrings = ArrayList<String>()
                for (location in locations) { locationStrings.add(location.formattedAddress) }
                val arrayAdapter =
                    ArrayAdapter(requireContext(), R.layout.item_autocomplete, locationStrings)
                binding.texteditAddress.setAdapter(arrayAdapter)

                if (locations.isNotEmpty() && address != locationStrings[0]) {
                    // Display the dropdown when at least 1 matching address has been found
                    binding.texteditAddress.showDropDown()
                } else if (locations.isNotEmpty() && address == locationStrings[0]) {
                    // If the address matches the suggestion, set the latlng
                    val loc = locations[0].geometry.location
                    listing.latLng = LatLng(loc.lat, loc.lng)
                    // TODO display an error message if the address doesn't match any suggestion
                }
            }
        }
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
            id = UUID.randomUUID().toString(),
            title = "",
            uri = uri,
            listingId = listing.id
        )
        viewModel.insert(newPhoto)
    }
}