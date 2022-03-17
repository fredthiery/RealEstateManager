package com.openclassrooms.realestatemanager.ui

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.*
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.models.MinMax
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels() {
        MainViewModelFactory((application as RealEstateManagerApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rightPaneNavController =
            (supportFragmentManager.findFragmentById(R.id.right_pane) as NavHostFragment).navController
        appBarConfiguration = AppBarConfiguration(rightPaneNavController.graph)
        setupActionBarWithNavController(rightPaneNavController, appBarConfiguration)

        val leftPaneNavController =
            (supportFragmentManager.findFragmentById(R.id.left_pane) as NavHostFragment).navController
        setupBottomNavMenu(leftPaneNavController)

        // Connect the SlidingPaneLayout to the system back button.
        onBackPressedDispatcher.addCallback(
            this,
            BackCallback()
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.right_pane)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_toolbar, menu)

        // Search
        val menuItem = menu?.findItem(R.id.action_search)
        menuItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                binding.filterChips.visibility = VISIBLE
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                binding.filterChips.visibility = GONE
                viewModel.resetSearch()
                binding.chipArea.isCloseIconVisible = false
                binding.chipPrice.isCloseIconVisible = false
                binding.chipRooms.isCloseIconVisible = false
                binding.chipPhotos.isCloseIconVisible = false
                binding.chipPoi.isCloseIconVisible = false
                return true
            }
        })

        val searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        searchView.setSearchableInfo(
            (getSystemService(Context.SEARCH_SERVICE) as SearchManager).getSearchableInfo(
                componentName
            )
        )
        searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.setSearchCriteria(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                viewModel.setSearchCriteria(query)
                return false
            }
        })

        setupBottomSheet(viewModel.searchCriteria.area, binding.chipArea, R.string.m2)
        setupBottomSheet(viewModel.searchCriteria.price, binding.chipPrice, R.string.dollar)
        setupBottomSheet(viewModel.searchCriteria.rooms, binding.chipRooms, R.string.rooms)
        setupBottomSheet(viewModel.searchCriteria.photos, binding.chipPhotos, R.string.photos)
        binding.chipPoi.setOnClickListener {
            BottomSheetSearchPOIs.newInstance(binding.chipPoi)
                .show(supportFragmentManager, BottomSheetSearchPOIs::class.java.canonicalName)
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun setupBottomSheet(criteria: MinMax, chip: Chip, unit: Int?) {
        chip.setOnClickListener {
            BottomSheetSearchMinMax.newInstance(criteria, chip, unit)
                .show(supportFragmentManager, BottomSheetSearchMinMax::class.java.canonicalName)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_new -> {
                binding.rightPane.findNavController().navigate(R.id.edit_dest)
                binding.slidingPaneLayout.openPane()
                viewModel.loadListing(null)
            }
            R.id.action_edit -> {}
            R.id.action_search -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNav?.setupWithNavController(navController)
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
            isEnabled = true
        }

        override fun onPanelClosed(panel: View) {
            isEnabled = false
        }
    }
}