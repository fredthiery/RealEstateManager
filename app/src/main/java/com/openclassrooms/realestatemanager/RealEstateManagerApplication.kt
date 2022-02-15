package com.openclassrooms.realestatemanager

import android.app.Application
import com.openclassrooms.realestatemanager.database.PropertyDatabase
import com.openclassrooms.realestatemanager.repositories.PropertyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class RealEstateManagerApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { PropertyDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { PropertyRepository(database.propertyDao()) }
}