package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.FragmentEditBinding
import com.openclassrooms.realestatemanager.models.Listing
import com.openclassrooms.realestatemanager.models.PointOfInterest
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!
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
            viewModel.loadListing(UUID.randomUUID().toString())
        else
            viewModel.loadListing(args.listingId!!)

        // Observe the models
        viewModel.currentListing.observe(viewLifecycleOwner, this::bind)
        viewModel.currentPhotos.observe(viewLifecycleOwner, adapter::submitList)
        viewModel.currentPOIs.observe(viewLifecycleOwner, this::updatePOIs)

        // Populate property type suggestions
        binding.texteditType.setAdapter(
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.property_types,
                R.layout.item_autocomplete
            )
        )

        // Populate address suggestions
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_autocomplete,
            ArrayList<String>()
        )
        binding.texteditAddress.setAdapter(arrayAdapter)
        viewModel.suggestions.observe(viewLifecycleOwner) { suggestions ->
            arrayAdapter.clear()
            arrayAdapter.addAll(suggestions.map { it.formattedAddress })
            arrayAdapter.filter.filter("")

            if (suggestions.isNotEmpty()
                && !binding.texteditAddress.text.toString().equals(suggestions[0].toString(), true)
            )
                binding.texteditAddress.showDropDown()
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

        // Show the bottom sheet when clicking on "Add a photo"
        binding.addPhoto.setOnClickListener {
            BottomSheetAddPhoto.newInstance().show(
                childFragmentManager,
                BottomSheetAddPhoto::class.java.canonicalName
            )
        }

        // Watch changes to textEdits
        binding.texteditType.doAfterTextChanged {
            viewModel.editListing.listing.type = it.toString()
        }
        binding.texteditRooms.doAfterTextChanged {
            viewModel.editListing.listing.numberOfRooms = it?.toString()?.toIntOrNull()
        }
        binding.texteditBedrooms.doAfterTextChanged {
            viewModel.editListing.listing.numberOfBedrooms = it?.toString()?.toIntOrNull()
        }
        binding.texteditBathrooms.doAfterTextChanged {
            viewModel.editListing.listing.numberOfBathrooms = it?.toString()?.toIntOrNull()
        }
        binding.texteditNeighborhood.doAfterTextChanged {
            viewModel.editListing.listing.neighborhood = it.toString()
        }
        binding.texteditDescription.doAfterTextChanged {
            viewModel.editListing.listing.description = it.toString()
        }
        binding.texteditPrice.doAfterTextChanged {
            viewModel.editListing.listing.price = it?.toString()?.toIntOrNull()
        }
        binding.texteditArea.doAfterTextChanged {
            viewModel.editListing.listing.area = it.toString().toIntOrNull()
        }
        binding.texteditAddress.doAfterTextChanged {
            viewModel.updateAddress(it.toString())
        }

        // Click on a date
        binding.texteditOnSaleDate.setOnClickListener { buttonSelectDate() }
    }

    private fun bind(listing: Listing) {
        binding.texteditType.setText(listing.type)
        binding.texteditDescription.setText(listing.description)
        binding.texteditNeighborhood.setText(listing.neighborhood)

        if (listing.address == "" && listing.latLng != LatLng(0.0, 0.0)) {
            lifecycleScope.launch {
                binding.texteditAddress.setText(viewModel.findAddress(listing.latLng))
            }
        } else binding.texteditAddress.setText(listing.address)

        binding.texteditRooms.setText(if (listing.numberOfRooms != null) listing.numberOfRooms.toString() else "")
        binding.texteditBedrooms.setText(if (listing.numberOfBedrooms != null) listing.numberOfBedrooms.toString() else "")
        binding.texteditBathrooms.setText(if (listing.numberOfBathrooms != null) listing.numberOfBathrooms.toString() else "")
        binding.texteditArea.setText(if (listing.area != null) listing.area.toString() else "")
        binding.texteditPrice.setText(if (listing.price != null) listing.price.toString() else "")

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

    private fun updatePOIs(it: List<PointOfInterest>) {
        binding.chipgroupPois.removeAllViews()
        for (place in it) {
            val chip = Chip(context)
            chip.text = String.format(
                resources.getString(R.string.chip),
                place.name,
                resources.getStringArray(R.array.poi_types)[place.type]
            )
            binding.chipgroupPois.addView(chip)
        }
    }
}