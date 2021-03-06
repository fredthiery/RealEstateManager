package com.openclassrooms.realestatemanager.repositories

import androidx.sqlite.db.SimpleSQLiteQuery
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.models.ListingFull
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.Place
import com.openclassrooms.realestatemanager.network.GoogleMapsApiService
import com.openclassrooms.realestatemanager.network.ListingDao
import com.openclassrooms.realestatemanager.utils.SearchCriteria
import kotlinx.coroutines.flow.Flow

class ListingRepository(
    private val listingDao: ListingDao,
    private val service: GoogleMapsApiService = GoogleMapsApiService.create()
) {

    val listings: Flow<List<ListingFull>> = listingDao.getAll()

    suspend fun searchListings(criteria: SearchCriteria): List<ListingFull> {
        val searchPoiTypes = criteria.pointsOfInterest.filter { it.value }.keys
        val term = criteria.term

        var query = "SELECT * FROM listing"
        query += " WHERE (type LIKE '%$term%' or description LIKE '%$term%' or address LIKE '%$term%')"
        criteria.area.min?.let { query += " AND (area >= $it)" }
        criteria.area.max?.let { query += " AND (area <= $it)" }
        criteria.price.min?.let { query += " AND (price >= $it)" }
        criteria.price.max?.let { query += " AND (price <= $it)" }
        criteria.rooms.min?.let { query += " AND (numberOfRooms >= $it)" }
        criteria.rooms.max?.let { query += " AND (numberOfRooms <= $it)" }
        query += " ORDER BY onSaleDate"

        return listingDao.searchListings(SimpleSQLiteQuery(query))
                .filter { it.photos.size >= criteria.photos.min ?: 0 }
                .filter { it.photos.size <= criteria.photos.max ?: Int.MAX_VALUE }
                .filter { listing ->
                    // Keeps only the listings containing all POIs requested
                    if (searchPoiTypes.isEmpty()) true
                    else {
                        val listingPoiTypes = listing.pois.map { it.type }
                        listingPoiTypes.containsAll(searchPoiTypes)
                    }
                }
    }

    fun getListing(id: Long): Flow<ListingFull> {
        return listingDao.getListing(id)
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

    suspend fun insert(listing: ListingFull) {
        val listingId = listingDao.insert(listing.listing)
        listingDao.insertPhotos(listing.photos.onEach { it.listingId = listingId })
        listingDao.insertPOIs(listing.pois.onEach { it.listingId = listingId })
    }

    suspend fun delete(photos: List<Photo>) {
        listingDao.delete(photos)
    }
}