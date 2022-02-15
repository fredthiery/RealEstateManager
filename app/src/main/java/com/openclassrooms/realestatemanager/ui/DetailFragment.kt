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
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels() {
        MainViewModelFactory((activity?.application as RealEstateManagerApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        binding.recyclerViewMedia.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = PhotoAdapter()
        binding.recyclerViewMedia.adapter = adapter

        viewModel.currentProperty.observe(viewLifecycleOwner) { property ->
            binding.detailDescription.text = property.description
            binding.detailAddress.text = property.address
            binding.detailRooms.text = property.numberOfRooms.toString()
            binding.detailSurface.text = property.area.toString()

            viewModel.getPhotos(property.id).observe(viewLifecycleOwner, adapter::submitList)

            binding.fabEdit.setOnClickListener {
                val action = DetailFragmentDirections.actionEdit(property.id)
                findNavController().navigate(action)
                activity?.findViewById<SlidingPaneLayout>(R.id.sliding_pane_layout)?.openPane()
            }
        }



        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}