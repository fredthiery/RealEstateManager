package com.openclassrooms.realestatemanager.models

import androidx.room.Embedded
import androidx.room.Relation

data class ListingFull(
    @Embedded
    var listing: Listing = Listing(),
    @Relation(
        parentColumn = "id",
        entityColumn = "listingId"
    )
    var photos: MutableList<Photo> = mutableListOf(),
    @Relation(
        parentColumn = "id",
        entityColumn = "listingId"
    )
    var pois: MutableList<PointOfInterest> = mutableListOf(),
    @Relation(
        parentColumn = "thumbnailId",
        entityColumn = "id"
    )
    var thumbnail: Photo? = null
) {
    fun copy(): ListingFull {
        return ListingFull(this.listing.copy(), this.photos.toMutableList(), this.pois.toMutableList())
    }
}
