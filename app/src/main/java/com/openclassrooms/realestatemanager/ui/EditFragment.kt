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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.FragmentEditBinding
import com.openclassrooms.realestatemanager.models.Listing
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
    private var latestTmpUri: Uri? = null
    private val args: EditFragmentArgs by navArgs()

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((activity?.application as RealEstateManagerApplication).repository)
    }

    private val adapter = PhotoAdapter() {
        // When clicking on a photo in the recyclerView, show the bottom sheet
        BottomSheetEditPhoto.newInstance(it).show(
            childFragmentManager,
            BottomSheetEditPhoto::class.java.canonicalName
        )
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
    ): View {
        _binding = FragmentEditBinding.inflate(inflater, container, false)

        // Recycler view for displaying photos
        binding.recyclerViewMedia.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewMedia.adapter = adapter

        // Edit an existing listing or create a new one
        if (args.listingId == null)
            viewModel.changeListing(UUID.randomUUID().toString())
        else
            viewModel.changeListing(args.listingId!!)

        viewModel.currentListing.observe(viewLifecycleOwner) {
            bind(it.listing)
            adapter.submitList(it.photos)

            binding.chipgroupPois.removeAllViews()
            for (place in it.pois) {
                val chip = Chip(context)
                chip.text = String.format(
                    resources.getString(R.string.chip),
                    place.name,
                    resources.getStringArray(R.array.poi_types)[place.type]
                )
                binding.chipgroupPois.addView(chip)
            }
        }

        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_autocomplete,
            ArrayList<String>()
        )
        binding.texteditAddress.setAdapter(arrayAdapter)

        // Populate the list of address suggestions
        viewModel.suggestions.observe(viewLifecycleOwner) { suggestions ->

            arrayAdapter.clear()
            arrayAdapter.addAll(suggestions.map { it.formattedAddress })
            arrayAdapter.filter.filter("")

            if (suggestions.isNotEmpty()) {
                if (viewModel.editListing.listing.address == suggestions[0].toString()) {
                    // If the address matches the suggestion, set the latlng
                    val loc = suggestions[0].toLatLng()
                    viewModel.editListing.listing.latLng = loc
                    viewModel.updatePOIs(loc)
                    // TODO display an error message if the address doesn't match any suggestion
                } else binding.texteditAddress.showDropDown()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Floating action button to save changes
        binding.fabSave.setOnClickListener {
            viewModel.saveListing()
            findNavController().navigate(R.id.action_save)
        }

        // Floating action buttons to add photos
        binding.fabTakePicture.setOnClickListener { takeImage() }
        binding.fabAddPicture.setOnClickListener { selectImageFromGallery() }

        // Watch changes to textEdits
        binding.texteditType.doAfterTextChanged {
            viewModel.editListing.listing.type = it.toString()
        }

        binding.texteditRooms.doAfterTextChanged {
            if (it != null && it.isNotEmpty()) viewModel.editListing.listing.numberOfRooms =
                it.toString().toInt()
        }
        binding.texteditBedrooms.doAfterTextChanged {
            if (it != null && it.isNotEmpty()) viewModel.editListing.listing.numberOfBedrooms =
                it.toString().toInt()
        }
        binding.texteditBathrooms.doAfterTextChanged {
            if (it != null && it.isNotEmpty()) viewModel.editListing.listing.numberOfBathrooms =
                it.toString().toInt()
        }

        binding.texteditDescription.doAfterTextChanged {
            viewModel.editListing.listing.description = it.toString()
        }
        binding.texteditPrice.doAfterTextChanged {
            if (it != null && it.isNotEmpty()) viewModel.editListing.listing.price =
                it.toString().toInt()
        }
        binding.texteditArea.doAfterTextChanged {
            if (it != null && it.isNotEmpty()) viewModel.editListing.listing.area =
                it.toString().toInt()
        }
        binding.texteditRooms.doAfterTextChanged {
            if (it != null && it.isNotEmpty()) viewModel.editListing.listing.numberOfRooms =
                it.toString().toInt()
        }
        binding.texteditAddress.doAfterTextChanged {
            viewModel.updateSuggestions(it.toString())
            viewModel.editListing.listing.address = it.toString()
        }

        // Click on a date
        binding.texteditOnSaleDate.setOnClickListener { buttonSelectDate() }
    }

    private fun bind(listing: Listing) {
        binding.texteditType.setText(listing.type)
        binding.texteditDescription.setText(listing.description)
        binding.texteditAddress.setText(listing.address)
        binding.texteditRooms.setText(listing.numberOfRooms.toString())
        binding.texteditBedrooms.setText(listing.numberOfBedrooms.toString())
        binding.texteditBathrooms.setText(listing.numberOfBathrooms.toString())
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
            viewModel.editListing.listing.onSaleDate = cal
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
            title = "",
            uri = uri,
            listingId = viewModel.editListing.listing.id
        )
        viewModel.add(newPhoto)
    }
}