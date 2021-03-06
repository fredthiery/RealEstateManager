package com.openclassrooms.realestatemanager.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ListingAdapter
    private lateinit var slidingPaneLayout: SlidingPaneLayout
    private val prefListener =
        SharedPreferences.OnSharedPreferenceChangeListener { preferences, key ->
            if (key.equals("currency")) adapter.notifyDataSetChanged()
        }

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((activity?.application as RealEstateManagerApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        slidingPaneLayout = requireActivity().findViewById(R.id.sliding_pane_layout)
        adapter = ListingAdapter(viewModel::loadDetails, viewModel, slidingPaneLayout)

        binding.listingList.adapter = adapter

        slidingPaneLayout.lockMode = SlidingPaneLayout.LOCK_MODE_LOCKED

        viewModel.listings.observe(viewLifecycleOwner, adapter::submitList)
        viewModel.currentListing.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(prefListener)

        // Add a divider between items
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        binding.listingList.addItemDecoration(dividerItemDecoration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

