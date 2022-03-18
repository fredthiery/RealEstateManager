package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.openclassrooms.realestatemanager.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}