package com.openclassrooms.realestatemanager.repositories

import androidx.annotation.WorkerThread
import com.openclassrooms.realestatemanager.database.ListingDao
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.Listing
import kotlinx.coroutines.flow.Flow

class ListingRepository(private val listingDao: ListingDao) {

    val listings: Flow<List<Listing>> = listingDao.getAll()

    fun getListing(id: String): Flow<Listing> {
        return listingDao.getListing(id)
    }

    fun getPhotos(listingId: String): Flow<List<Photo>> {
        return listingDao.getPhotos(listingId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(listing: Listing) {
        listingDao.insert(listing)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(listing: Listing) {
        listingDao.update(listing)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(photo: Photo) {
        listingDao.insert(photo)
    }
}