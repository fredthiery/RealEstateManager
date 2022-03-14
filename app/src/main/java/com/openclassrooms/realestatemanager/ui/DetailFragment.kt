package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory
import java.text.NumberFormat

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val nFormat: NumberFormat = NumberFormat.getInstance()

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
        viewModel.currentListing.observe(viewLifecycleOwner) { listing ->
            bind(listing)
            initLiteMap(listing)

            // Edit button
            binding.fabEdit.setOnClickListener {
                val action = DetailFragmentDirections.actionEdit(listing.id)
                findNavController().navigate(action)
                activity?.findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)?.openPane()
            }
        }

        // Observe Photos
        viewModel.currentPhotos.observe(viewLifecycleOwner) { photos ->
            binding.layoutMedia.visibility = if (photos.isNotEmpty()) View.VISIBLE else View.GONE
            adapter.submitList(photos)
        }

        // Observe Points of interest
        viewModel.currentPOIs.observe(viewLifecycleOwner) { pois ->
            bind(pois)
        }
    }

    private fun bind(listing: Listing) {
        binding.title.text = String.format(
            resources.getString(R.string.listing_title),
            listing.type,
            listing.neighborhood
        )

        binding.detailDescription.text = listing.description

        listing.price?.let {
            binding.detailPrice.text = String.format(
                resources.getString(R.string.price_format),
                nFormat.format(it)
            )
        }

        binding.detailAddress.text = listing.address.replace(", ", "\n")
        binding.detailRooms.text = listing.numberOfRooms.toString()
        binding.detailBedrooms.text = listing.numberOfBedrooms.toString()
        binding.detailBathrooms.text = listing.numberOfBathrooms.toString()
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
        val poiMap = LinkedHashMap<String, Int>()
        val types = resources.getStringArray(R.array.poi_types)

        for (place in pois) {
            val x = poiMap[types[place.type]]
            poiMap[types[place.type]] = (x ?: 0) + 1
        }

        for (place in poiMap) {
            val chip = Chip(context)
            chip.text = String.format(
                resources.getString(R.string.chip),
                place.key,
                place.value
            )
            binding.chipgroupPois.addView(chip)
        }
    }

    private fun initLiteMap(listing: Listing) {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_lite) as SupportMapFragment?
        mapFragment?.getMapAsync { gMap ->
            gMap.addMarker(
                MarkerOptions()
                    .position(listing.latLng)
                    .title(listing.title)
            )
            gMap.moveCamera(CameraUpdateFactory.newLatLng(listing.latLng))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}