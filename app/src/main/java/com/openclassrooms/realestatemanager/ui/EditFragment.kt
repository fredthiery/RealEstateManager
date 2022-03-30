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
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.FragmentEditBinding
import com.openclassrooms.realestatemanager.models.Listing
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.PointOfInterest
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((activity?.application as RealEstateManagerApplication).repository)
    }

    private lateinit var adapter: PhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBinding.inflate(inflater, container, false)

        adapter = PhotoAdapter(viewModel, true) {
            // When clicking on a photo in the recyclerView, show the bottom sheet
            if (it.uri == null) {
                BottomSheetAddPhoto.newInstance().show(
                    childFragmentManager,
                    BottomSheetAddPhoto::class.java.canonicalName
                )
            } else {
                BottomSheetEditPhoto.newInstance(it).show(
                    childFragmentManager,
                    BottomSheetEditPhoto::class.java.canonicalName
                )
            }
        }

        // Recycler view for displaying photos
        binding.recyclerViewMedia.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewMedia.adapter = adapter

        // Observe the models
        viewModel.currentListing.observe(viewLifecycleOwner, this::bind)
        viewModel.currentPhotos.observe(viewLifecycleOwner) {
            val list = it.toMutableList()
            list.add(0, Photo(id = 0L, title = resources.getString(R.string.add_a_photo), null, 0L))
            adapter.submitList(list)
        }
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
            context?.let {
                val preferences = PreferenceManager.getDefaultSharedPreferences(it)
                viewModel.editListing.listing.realtor = preferences.getString("realtor_name", "")
            }
            viewModel.saveListing(resources.getString(R.string.listing_saved))
            findNavController().navigateUp()
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
        binding.texteditArea.doAfterTextChanged {
            viewModel.editListing.listing.area = it.toString().toIntOrNull()
        }
        binding.texteditAddress.doAfterTextChanged {
            lifecycleScope.launch {
                val latLng = viewModel.updateAddress(it.toString())
                initLiteMap(latLng, viewModel.editListing.listing.title)
            }
        }

        context?.let { context ->
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            // Register text watcher depending on selected currency
            binding.texteditPrice.doAfterTextChanged(preferences.getString("currency", "dollar"))
        }

        // Click on a date
        binding.texteditOnSaleDate.selectDate { viewModel.editListing.listing.onSaleDate = it }
        binding.texteditSellDate.selectDate { viewModel.editListing.listing.sellDate = it }
    }

    private fun TextInputEditText.doAfterTextChanged(currency: String?) {
        when (currency) {
            "euro" -> {
                doAfterTextChanged {
                    viewModel.editListing.listing.price =
                        Utils.convertEuroToDollar(it?.toString()?.toIntOrNull() ?: 0)
                }
            }
            else -> {
                doAfterTextChanged {
                    viewModel.editListing.listing.price = it?.toString()?.toIntOrNull()
                }
            }
        }
    }

    private fun bind(listing: Listing) {
        adapter.notifyDataSetChanged()
        binding.texteditType.setText(listing.type)
        binding.texteditDescription.setText(listing.description)
        binding.texteditNeighborhood.setText(listing.neighborhood)

        if (listing.address == "" && listing.latLng != null) {
            lifecycleScope.launch {
                binding.texteditAddress.setText(viewModel.findAddress(listing.latLng!!))
            }
        } else binding.texteditAddress.setText(listing.address)

        binding.texteditRooms.setText(if (listing.numberOfRooms != null) listing.numberOfRooms.toString() else "")
        binding.texteditBedrooms.setText(if (listing.numberOfBedrooms != null) listing.numberOfBedrooms.toString() else "")
        binding.texteditBathrooms.setText(if (listing.numberOfBathrooms != null) listing.numberOfBathrooms.toString() else "")
        binding.texteditArea.setText(if (listing.area != null) listing.area.toString() else "")

        context?.let {
            bindPrice(
                PreferenceManager
                    .getDefaultSharedPreferences(it)
                    .getString("currency", "dollar"), listing
            )
        }

        binding.texteditOnSaleDate.setTime(listing.onSaleDate.time)
        listing.sellDate?.let { binding.texteditSellDate.setTime(it.time) }

        initLiteMap(listing.latLng, listing.title)
    }

    private fun bindPrice(currency: String?, listing: Listing) {
        when (currency) {
            "euro" -> {
                binding.layoutPrice.prefixText = ""
                binding.layoutPrice.suffixText = "€"
                binding.texteditPrice.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
                binding.texteditPrice.setText(
                    if (listing.price != null)
                        (Utils.convertDollarToEuro(listing.price!!)).toString()
                    else ""
                )
            }
            else -> {
                binding.layoutPrice.prefixText = "$"
                binding.layoutPrice.suffixText = ""
                binding.texteditPrice.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                binding.texteditPrice.setText(if (listing.price != null) listing.price.toString() else "")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun TextInputEditText.selectDate(callback: (cal: Calendar) -> Unit) {
        setOnClickListener {
            val cal = Calendar.getInstance()
            val builder = MaterialDatePicker.Builder.datePicker()
            builder.setSelection(cal.timeInMillis)
            val datePicker = builder.build()
            datePicker.addOnPositiveButtonClickListener {
                cal.timeInMillis = it
                setTime(cal.time)
                callback(cal)
            }
            activity?.let { datePicker.show(it.supportFragmentManager, "") }
        }
    }

    private fun TextInputEditText.setTime(date: Date) {
        setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date))
    }

    private fun updatePOIs(it: List<PointOfInterest>) = lifecycleScope.launch {
        binding.chipgroupPois.removeAllViews()
        val sortedPOIs = it.sortedBy { it.type }
        for (place in sortedPOIs) {
            val chip = Chip(context)
            chip.text = String.format(
                resources.getString(R.string.chip),
                resources.getStringArray(R.array.poi_types)[place.type],
                place.name
            )
            binding.chipgroupPois.addView(chip)
        }
    }

    private fun initLiteMap(latLng: LatLng?, title: String) {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_lite) as SupportMapFragment?

        binding.cardMap.visibility = if (latLng != null) View.VISIBLE else View.GONE

        latLng?.let {
            mapFragment?.getMapAsync { gMap ->
                gMap.setOnMapClickListener {}
                gMap.uiSettings.isMapToolbarEnabled = false
                gMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(title)
                )
                gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            }
        }
    }
}