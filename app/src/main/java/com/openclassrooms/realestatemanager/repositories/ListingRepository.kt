package com.openclassrooms.realestatemanager.repositories

import androidx.annotation.WorkerThread
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.database.ListingDao
import com.openclassrooms.realestatemanager.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class ListingRepository(private val listingDao: ListingDao) {

    private val service = GoogleMapsApiService.create()

    val listings: Flow<List<Listing>> = listingDao.getAll().onEach { getThumbnail(it) }

    fun searchListings(
        search: String,
        area: MinMax,
        price: MinMax,
        rooms: MinMax
    ): Flow<List<Listing>> {
        return listingDao.searchListings(
            "%$search%",
            area.min, area.max,
            price.min, price.max,
            rooms.min, rooms.max
        ).onEach { getThumbnail(it) }
    }

    fun getListing(id: String): Flow<ListingWithPhotos> {
        return listingDao.getListing(id).map { it ?: ListingWithPhotos() }
    }

    suspend fun getLocation(address: String): List<Place> {
        return service.getPlaceByAddress(address).results
    }

    suspend fun getLocation(latLng: LatLng): List<Place> {
        return service.getPlaceByLatLng("${latLng.latitude},${latLng.longitude}").results
    }

    suspend fun getNearbyPOIs(latLng: LatLng): List<Place> {
        return service.getNearbyPOIs("${latLng.latitude},${latLng.longitude}").results
    }

    suspend fun insert(listing: ListingWithPhotos) {
        listingDao.insert(listing.listing)
        listingDao.insertPhotos(listing.photos)
        listingDao.insertPOIs(listing.pois)
    }

    private suspend fun getThumbnail(it: List<Listing>) {
        for (listing in it) listing.thumbnail = listingDao.getPhoto(listing.thumbnailId)
    }
}