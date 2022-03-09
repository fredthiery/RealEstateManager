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
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory
import java.text.NumberFormat

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val nFormat: NumberFormat = NumberFormat.getInstance()

    private val viewModel: MainViewModel by activityViewModels() {
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

        val adapter = PhotoAdapter() {}
        binding.recyclerViewMedia.adapter = adapter

        viewModel.currentListing.observe(viewLifecycleOwner) { item ->
            // Main data
            binding.detailDescription.text = item.listing.description
            binding.detailPrice.text = String.format(
                resources.getString(R.string.price_format),
                nFormat.format(item.listing.price)
            )
            binding.detailAddress.text = item.listing.address.replace(", ", "\n")
            binding.detailRooms.text = item.listing.numberOfRooms.toString()
            binding.detailBedrooms.text = item.listing.numberOfBedrooms.toString()
            binding.detailBathrooms.text = item.listing.numberOfBathrooms.toString()
            binding.detailSurface.text = item.listing.area.toString()

            // Photos
            binding.layoutMedia.visibility = if (item.photos.size > 0) View.VISIBLE else View.GONE
            adapter.submitList(item.photos)

            // Points of interest
            binding.layoutPois.visibility = if (item.pois.size > 0) View.VISIBLE else View.GONE
            binding.chipgroupPois.removeAllViews()
            for (place in item.pois) {
                val chip = Chip(context)
                chip.text = String.format(
                    resources.getString(R.string.chip),
                    place.name,
                    resources.getStringArray(R.array.poi_types)[place.type]
                )
                binding.chipgroupPois.addView(chip)
            }

            // Lite Map
            val mapFragment = childFragmentManager.findFragmentById(R.id.map_lite) as SupportMapFragment?
            mapFragment?.getMapAsync { gMap ->
                gMap.addMarker(
                    MarkerOptions()
                        .position(item.listing.latLng)
                        .title(item.listing.title)
                )
                gMap.moveCamera(CameraUpdateFactory.newLatLng(item.listing.latLng))
            }

            // Edit button
            binding.fabEdit.setOnClickListener {
                val action = DetailFragmentDirections.actionEdit(item.listing.id)
                findNavController().navigate(action)
                activity?.findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)?.openPane()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}