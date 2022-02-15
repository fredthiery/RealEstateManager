package com.openclassrooms.realestatemanager.repositories

import androidx.annotation.WorkerThread
import com.openclassrooms.realestatemanager.database.PropertyDao
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.PropertyShort
import kotlinx.coroutines.flow.Flow

class PropertyRepository(private val propertyDao: PropertyDao) {

    val allProperties: Flow<List<Property>> = propertyDao.getAll()
    val allShort: Flow<List<PropertyShort>> = propertyDao.getAllShort()

    fun getProperty(id: String): Flow<Property> {
        return propertyDao.getProperty(id)
    }

    fun getPhotos(propertyId: String): Flow<List<Photo>> {
        return propertyDao.getPhotos(propertyId)
    }

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

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertPhoto(photo: Photo) {
        propertyDao.insertPhoto(photo)
    }
}