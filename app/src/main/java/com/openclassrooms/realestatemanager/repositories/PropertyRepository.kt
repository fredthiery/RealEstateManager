package com.openclassrooms.realestatemanager.repositories

import androidx.annotation.WorkerThread
import com.openclassrooms.realestatemanager.database.PropertyDao
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.coroutines.flow.Flow

class PropertyRepository(private val propertyDao: PropertyDao) {

    val allProperties: Flow<List<Property>> = propertyDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(property: Property) {
        propertyDao.insert(property)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(property: Property) {
        propertyDao.update(property)
    }
}