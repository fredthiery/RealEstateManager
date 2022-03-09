package com.openclassrooms.realestatemanager.models

import androidx.room.Embedded
import androidx.room.Relation

data class ListingWithPhotos(
    @Embedded
    val listing: Listing = Listing(),
    @Relation(
        parentColumn = "id",
        entityColumn = "listingId"
    )
    var photos: MutableList<Photo> = mutableListOf(),
    @Relation(
        parentColumn = "id",
        entityColumn = "listingId"
    )
    var pois: MutableList<PointOfInterest> = mutableListOf()
) {
    fun copy(): ListingWithPhotos {
        return ListingWithPhotos(this.listing.copy(), this.photos.toMutableList(), this.pois.toMutableList())
    }
}
