package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.FragmentSlidingPaneBinding
import com.openclassrooms.realestatemanager.utils.MinMax
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory

class SlidingPaneFragment : Fragment() {

    private var _binding: FragmentSlidingPaneBinding? = null
    private val binding get() = _binding!!
    private var myMenu: Menu? = null

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((activity?.application as RealEstateManagerApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlidingPaneBinding.inflate(inflater, container, false)

        binding.filterChips.visibility = View.GONE

        // Connect the SlidingPaneLayout to the system back button.
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            BackCallback()
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.left_pane) as NavHostFragment
        binding.bottomNavView.setupWithNavController(navHostFragment.navController)

        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_new -> {
                viewModel.editListing()
                binding.rightPane.findNavController().navigate(R.id.edit_dest)
                binding.slidingPaneLayout.openPane()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        myMenu = menu
        inflater.inflate(R.menu.toolbar_detail, menu)

        // Search
        val searchView = menu.findItem(R.id.action_search)?.actionView as SearchView
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.setSearchCriteria(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                viewModel.setSearchCriteria(query)
                return false
            }
        })
        val menuItem = menu.findItem(R.id.action_search)

        menuItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                binding.filterChips.visibility = SearchView.VISIBLE
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                binding.filterChips.visibility = SearchView.GONE
                viewModel.resetSearch()
                binding.chipArea.isCloseIconVisible = false
                binding.chipPrice.isCloseIconVisible = false
                binding.chipRooms.isCloseIconVisible = false
                binding.chipPhotos.isCloseIconVisible = false
                binding.chipPoi.isCloseIconVisible = false
                return true
            }
        })

        // Setup filter chips
        setupBottomSheet(viewModel.searchCriteria.area, binding.chipArea, R.string.m2)
        setupBottomSheet(viewModel.searchCriteria.price, binding.chipPrice, R.string.dollar)
        setupBottomSheet(viewModel.searchCriteria.rooms, binding.chipRooms, R.string.rooms)
        setupBottomSheet(viewModel.searchCriteria.photos, binding.chipPhotos, R.string.photos)
        binding.chipPoi.setOnClickListener {
            BottomSheetSearchPOIs.newInstance(binding.chipPoi)
                .show(parentFragmentManager, BottomSheetSearchPOIs::class.java.canonicalName)
        }

        return super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupBottomSheet(criteria: MinMax, chip: Chip, unit: Int?) {
        chip.setOnClickListener {
            BottomSheetSearchMinMax.newInstance(criteria, chip, unit).show(
                parentFragmentManager,
                BottomSheetSearchMinMax::class.java.canonicalName
            )
        }
    }


    inner class BackCallback() :
        OnBackPressedCallback(binding.slidingPaneLayout.isSlideable && binding.slidingPaneLayout.isOpen),
        SlidingPaneLayout.PanelSlideListener {

        init {
            binding.slidingPaneLayout.addPanelSlideListener(this)
        }

        override fun handleOnBackPressed() {
            binding.slidingPaneLayout.closePane()
        }

        override fun onPanelSlide(panel: View, slideOffset: Float) {}

        override fun onPanelOpened(panel: View) {
            myMenu?.findItem(R.id.action_search)?.isVisible = false
            myMenu?.findItem(R.id.action_new)?.isVisible = false

            (activity as MainActivity).showUpButton(true)

            isEnabled = true
        }

        override fun onPanelClosed(panel: View) {
            myMenu?.findItem(R.id.action_search)?.isVisible = true
            myMenu?.findItem(R.id.action_new)?.isVisible = true

            (activity as MainActivity).showUpButton(false)

            isEnabled = false
        }
    }
}