package com.openclassrooms.realestatemanager

import android.app.Application
import com.openclassrooms.realestatemanager.network.ListingDatabase
import com.openclassrooms.realestatemanager.repositories.ListingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RealEstateManagerApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { ListingDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { ListingRepository(database.listingDao()) }
}