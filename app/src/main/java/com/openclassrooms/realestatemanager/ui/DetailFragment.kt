package com.openclassrooms.realestatemanager.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.models.Listing
import com.openclassrooms.realestatemanager.models.PointOfInterest
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory
import java.text.NumberFormat

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val nFormat: NumberFormat = NumberFormat.getInstance()
    private val prefListener =
        SharedPreferences.OnSharedPreferenceChangeListener { preferences, key ->
            if (key.equals("currency")) viewModel.detailListing.value?.let { bind(it.listing) }
        }

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((activity?.application as RealEstateManagerApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        binding.recyclerViewMedia.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PhotoAdapter {}
        binding.recyclerViewMedia.adapter = adapter

        // Observe Listing
        viewModel.detailListing.observe(viewLifecycleOwner) { listing ->
            bind(listing.listing)
            initLiteMap(listing.listing)

            binding.layoutMedia.visibility =
                if (listing.photos.isNotEmpty()) View.VISIBLE else View.GONE
            adapter.submitList(listing.photos)

            bind(listing.pois)

            // Edit button
            binding.fabEdit.setOnClickListener {
                findNavController().navigate( R.id.action_edit )
                activity?.findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)?.openPane()
            }
        }

        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(prefListener)

        setHasOptionsMenu(true)
    }

    private fun bind(listing: Listing) {
        binding.title.text = String.format(
            resources.getString(R.string.listing_title),
            listing.type,
            listing.neighborhood
        )

        binding.detailDescription.text = listing.description

        // Display price in dollar or euro depending on preference
        listing.price?.let {
            context?.let { context ->
                val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                when (preferences.getString("currency", "dollar")) {
                    "euro" -> binding.detailPrice.text = String.format(
                        resources.getString(R.string.price_format_euro),
                        nFormat.format(Utils.convertDollarToEuro(it))
                    )
                    else -> binding.detailPrice.text = String.format(
                        resources.getString(R.string.price_format_dollar),
                        nFormat.format(it)
                    )
                }
            }
        }

        binding.detailAddress.text = listing.address.replace(", ", "\n")
        binding.detailRooms.text = listing.numberOfRooms.toString()
        binding.detailBedrooms.text = listing.numberOfBedrooms.toString()
        binding.detailBathrooms.text = listing.numberOfBathrooms.toString()
        binding.detailRealtor.text = listing.realtor

        binding.detailArea.text =
            String.format(resources.getString(R.string.area_format), listing.area.toString())

        binding.layoutPrice.visibility = if (listing.price != null) View.VISIBLE else View.GONE
        binding.layoutArea.visibility = if (listing.area != null) View.VISIBLE else View.GONE
        binding.layoutRooms.visibility =
            if (listing.numberOfRooms != null) View.VISIBLE else View.GONE
        binding.layoutBedrooms.visibility =
            if (listing.numberOfBedrooms != null) View.VISIBLE else View.GONE
        binding.layoutBathrooms.visibility =
            if (listing.numberOfBathrooms != null) View.VISIBLE else View.GONE
    }


    private fun bind(pois: List<PointOfInterest>) {
        binding.layoutPois.visibility = if (pois.isNotEmpty()) View.VISIBLE else View.GONE
        binding.chipgroupPois.removeAllViews()

        viewModel.getSimplifiedPois(pois, resources.getStringArray(R.array.poi_types)).forEach {
            val chip = Chip(context)
            chip.text = String.format(resources.getString(R.string.chip), it.key, it.value)
            binding.chipgroupPois.addView(chip)
        }
    }

    private fun initLiteMap(listing: Listing) {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_lite) as SupportMapFragment?

        binding.cardMap.visibility = if (listing.latLng != null) View.VISIBLE else View.GONE

        listing.latLng?.let { latLng ->
            mapFragment?.getMapAsync { gMap ->
                gMap.setOnMapClickListener {}
                gMap.uiSettings.isMapToolbarEnabled = false
                gMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(listing.title)
                )
                gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}