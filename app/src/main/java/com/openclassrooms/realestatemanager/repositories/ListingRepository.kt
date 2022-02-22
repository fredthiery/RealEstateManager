package com.openclassrooms.realestatemanager.repositories

import androidx.annotation.WorkerThread
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.database.ListingDao
import com.openclassrooms.realestatemanager.models.Listing
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.geoCodingResponse.Place
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ListingRepository(private val listingDao: ListingDao) {

    val listings: Flow<List<Listing>> = listingDao.getAll()

    private val service = GeoCodingService.create()

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

    fun getLocation(address: String): Flow<List<Place>> {
        return flow {
            emit(service.getPlaceByAddress(address).results)
        }
    }

    fun getLocation(latLng: LatLng): Flow<List<Place>> {
        return flow {
            emit(service.getPlaceByLatLng("${latLng.latitude},${latLng.longitude}").results)
        }
    }
}