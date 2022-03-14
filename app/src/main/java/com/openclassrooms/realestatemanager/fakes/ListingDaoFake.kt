package com.openclassrooms.realestatemanager.fakes

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.database.ListingDao
import com.openclassrooms.realestatemanager.models.Listing
import com.openclassrooms.realestatemanager.models.ListingWithPhotos
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.PointOfInterest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ListingDaoFake : ListingDao {
    private val listingList = mutableListOf(
        Listing(id = "0", type = "House"),
        Listing(id = "1", type = "Apartment"),
        Listing(id = "2", type = "House"))
    private val photoList = mutableListOf(
        Photo(id = "3", uri = Uri.EMPTY, listingId = "0"),
        Photo(id = "4", uri = Uri.EMPTY, listingId = "1"),
        Photo(id = "5", uri = Uri.EMPTY, listingId = "2"))
    private val poiList = mutableListOf(
        PointOfInterest(id = "6", name = "Eiffel Tower", type = 4, latLng = LatLng(0.0,0.0), listingId = "0"),
        PointOfInterest(id = "7", name = "Arc de triomphe", type = 6, latLng = LatLng(0.1, 0.1), listingId = "1"),
        PointOfInterest(id = "8", name = "Statue de la liberté", type = 7, latLng = LatLng(0.5, 0.2), listingId = "2"))

    override fun getAll(): Flow<List<Listing>> {
        return flowOf(listingList)
    }

    override fun searchListings(
        search: String,
        areaMin: Int,
        areaMax: Int,
        priceMin: Int,
        priceMax: Int,
        roomsMin: Int,
        roomsMax: Int
    ): Flow<List<Listing>> {
        return flowOf(listOf(listingList[1]))
    }

    override fun getListing(listingId: String): Flow<ListingWithPhotos> {
        return flowOf(
            ListingWithPhotos(
                listingList[0],
                mutableListOf(photoList[1]),
                mutableListOf(poiList[2])
            )
        )
    }

    override fun getPhotos(listingId: String): Flow<List<Photo>> {
        return flowOf(photoList)
    }

    override suspend fun getPhoto(id: String?): Photo? {
        return photoList[1]
    }

    override suspend fun insert(listing: Listing) {
        listingList.add(listing)
    }

    override suspend fun insert(photo: Photo) {
        photoList.add(photo)
    }

    override suspend fun update(listing: Listing) {
        listingList[1] = listing
    }

    override suspend fun insertPhotos(photos: MutableList<Photo>) {
        photoList.addAll(photos)
    }

    override suspend fun insertPOIs(pois: MutableList<PointOfInterest>) {
        poiList.addAll(pois)
    }

    override suspend fun delete(photo: Photo) {
        photoList.remove(photo)
    }
}