package com.openclassrooms.realestatemanager.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.FragmentMapsBinding
import com.openclassrooms.realestatemanager.models.Listing
import com.openclassrooms.realestatemanager.models.ListingFull
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var clusterManager: ClusterManager<Listing>

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((activity?.application as RealEstateManagerApplication).repository)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                enableMyLocation()
            } else {
                Toast.makeText(context, "Missing location permission", Toast.LENGTH_LONG).show()
            }
        }

    @SuppressLint("PotentialBehaviorOverride")
    private val callback = OnMapReadyCallback { gMap ->
        googleMap = gMap

        clusterManager = ClusterManager(context, googleMap)
        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)

        // Cluster click
        clusterManager.setOnClusterClickListener { cluster ->
            centerCameraAround(cluster.items)
            true
        }

        // Marker click
        clusterManager.setOnClusterItemClickListener { listing ->
            viewModel.loadListing(listing.id)
            true
        }

        // Map click
        googleMap.setOnMapLongClickListener {
            activity?.findNavController(R.id.right_pane)?.navigate(R.id.edit_dest)
            viewModel.setCurrentListing(ListingFull(Listing(latLng = it)))
            val slidingPaneLayout =
                requireActivity().findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)
            slidingPaneLayout.openPane()
        }

        enableMyLocation()
        viewModel.listings.observe(viewLifecycleOwner) { listings ->
            updateMarkers(listings.map { it.listing })
            centerCameraAround(listings.map { it.listing })
        }
    }

    private fun centerCameraAround(listings: Collection<Listing>) {
        if (listings.isNotEmpty()) {
            val builder = LatLngBounds.builder()
            for (listing in listings) {
                listing.latLng?.let { builder.include(it) }
            }
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(builder.build(), 200),
                500,
                null
            )
        }
    }

    private fun updateMarkers(listings: List<Listing>) {
        clusterManager.clearItems()
        clusterManager.addItems(listings)
        clusterManager.cluster()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the view
        _binding = FragmentMapsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        // Initialize the location provider
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isLocationPermissionGranted()) {
            googleMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}