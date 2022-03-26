package com.openclassrooms.realestatemanager.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.RealEstateManagerApplication
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding
import com.openclassrooms.realestatemanager.utils.MinMax
import com.openclassrooms.realestatemanager.viewmodels.MainViewModel
import com.openclassrooms.realestatemanager.viewmodels.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel: MainViewModel by viewModels() {
        MainViewModelFactory((application as RealEstateManagerApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.filterChips.visibility = GONE

        val rightPaneNavController =
            (supportFragmentManager.findFragmentById(R.id.right_pane) as NavHostFragment).navController
        appBarConfiguration = AppBarConfiguration(rightPaneNavController.graph)
        setupActionBarWithNavController(rightPaneNavController, appBarConfiguration)

        binding.bottomNavView.setupWithNavController(
            (supportFragmentManager.findFragmentById(R.id.left_pane) as NavHostFragment).navController
        )

        onBackPressedDispatcher.addCallback(this, BackCallback())

        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = binding.rightPane.findNavController()
        binding.slidingPaneLayout.closePane()
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

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
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
            BottomSheetSearchMinMax.newInstance(criteria, chip, unit).show(
                supportFragmentManager,
                BottomSheetSearchMinMax::class.java.canonicalName
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_new -> {
                binding.rightPane.findNavController().navigate(R.id.edit_dest)
                binding.slidingPaneLayout.openPane()
                viewModel.editListing()
            }
            R.id.action_search -> {}
            R.id.action_settings -> {
                val settingsActivity = Intent(this, SettingsActivity::class.java)
                startActivity(settingsActivity)
            }
            R.id.action_loan_calculator -> {
                val loanCalculatorActivity = Intent(this, LoanCalculatorActivity::class.java)
                startActivity(loanCalculatorActivity)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showUpButton(b: Boolean) {
        supportActionBar!!.setDisplayHomeAsUpEnabled(b)
    }

    inner class BackCallback() :
        OnBackPressedCallback(binding.slidingPaneLayout.isSlideable && binding.slidingPaneLayout.isOpen),
        SlidingPaneLayout.PanelSlideListener {

        init {
            binding.slidingPaneLayout.addPanelSlideListener(this)
        }

        override fun handleOnBackPressed() {
            binding.slidingPaneLayout.closePane()
            binding.rightPane.findNavController().navigateUp()
        }

        override fun onPanelSlide(panel: View, slideOffset: Float) {}

        override fun onPanelOpened(panel: View) {
            showUpButton(true)
            isEnabled = true
        }

        override fun onPanelClosed(panel: View) {
            showUpButton(false)
            isEnabled = false
        }
    }
}