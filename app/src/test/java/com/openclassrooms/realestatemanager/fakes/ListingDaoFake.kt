package com.openclassrooms.realestatemanager.fakes

import android.net.Uri
import androidx.sqlite.db.SupportSQLiteQuery
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.models.Listing
import com.openclassrooms.realestatemanager.models.ListingFull
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.PointOfInterest
import com.openclassrooms.realestatemanager.network.ListingDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ListingDaoFake : ListingDao {
    private val listing1 =
        Listing(id = "listing0", type = "House", address = "1 rue de nulle part, 75001 Paris")
    private val listing2 =
        Listing(id = "listing1", type = "Apartment", address = "2 rue quelconque, 75002 Paris")
    private val listing3 =
        Listing(id = "listing2", type = "House", address = "3 rue inconnue, 75003 Paris")

    private val photo1 = Photo(id = "photo0", uri = Uri.parse("testuri/1"), listingId = "0")
    private val photo2 = Photo(id = "photo1", uri = Uri.parse("testuri/2"), listingId = "1")
    private val photo3 = Photo(id = "photo2", uri = Uri.parse("testuri/3"), listingId = "2")

    private val poi1 = PointOfInterest(
        id = "poi0",
        name = "Eiffel Tower",
        type = 4,
        latLng = LatLng(0.0, 0.0),
        listingId = "0"
    )
    private val poi2 = PointOfInterest(
        id = "poi1",
        name = "Arc de triomphe",
        type = 6,
        latLng = LatLng(0.1, 0.1),
        listingId = "1"
    )
    private val poi3 = PointOfInterest(
        id = "poi2",
        name = "Statue de la liberté",
        type = 7,
        latLng = LatLng(0.5, 0.2),
        listingId = "2"
    )

    private val listingList = mutableListOf(
        ListingFull(listing1, mutableListOf(photo1), mutableListOf(poi1)),
        ListingFull(listing2, mutableListOf(photo2), mutableListOf(poi2)),
        ListingFull(listing3, mutableListOf(photo1, photo3), mutableListOf(poi3)),
    )

    override fun getAll(): Flow<List<ListingFull>> {
        return flowOf(listingList)
    }

    override suspend fun searchListings(query: SupportSQLiteQuery): List<ListingFull> {
        return listingList
    }

    override fun getListing(listingId: String): Flow<ListingFull> {
        return flowOf(listingList.find { it.listing.id == listingId }!!)
    }

    override fun getPhotos(listingId: String): Flow<List<Photo>> {
        return flowOf(listingList.find { it.listing.id == listingId }!!.photos)
    }

    override suspend fun getPhoto(id: String?): Photo? {
        for (listing in listingList) {
            return listing.photos.find { it.id == id }
        }
        return null
    }

    override suspend fun insert(listing: Listing) {
        listingList.add(ListingFull(listing))
    }

    override suspend fun update(listing: Listing) {
        listingList.find { it.listing.id == listing.id }?.listing = listing
    }

    override suspend fun insert(photo: Photo) {
        listingList.find { it.listing.id == photo.listingId }?.photos?.add(photo)
    }

    override suspend fun insertPhotos(photos: MutableList<Photo>) {
        for (photo in photos) {
            insert(photo)
        }
    }

    override suspend fun insertPOIs(pois: MutableList<PointOfInterest>) {
        for (poi in pois) {
            listingList.find { it.listing.id == poi.listingId }?.pois?.add(poi)
        }
    }

    override suspend fun delete(photos: List<Photo>) {
        for (listing in listingList) {
            listing.photos.removeAll(photos)
        }
    }
}