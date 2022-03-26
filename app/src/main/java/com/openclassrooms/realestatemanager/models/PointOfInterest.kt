package com.openclassrooms.realestatemanager.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.util.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = Listing::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("listingId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class PointOfInterest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: Int,
    val latLng: LatLng,
    @ColumnInfo(index = true) var listingId: Long
) {
    companion object {
        val filter = listOf(
            "airport",
            "amusement_park",
            "aquarium",
            "art_gallery",
            "bakery",
            "bank",
            "bar",
            "beauty_salon",
            "bicycle_store",
            "book_store",
            "bowling_alley",
            "cafe",
            "casino",
            "church",
            "city_hall",
            "clothing_store",
            "convenience_store",
            "department_store",
            "doctor",
            "drugstore",
            "electronics_store",
            "embassy",
            "fire_station",
            "florist",
            "furniture_store",
            "gym",
            "hair_care",
            "hardware_store",
            "home_goods_store",
            "hospital",
            "library",
            "mosque",
            "movie_theater",
            "museum",
            "night_club",
            "park",
            "parking",
            "pet_store",
            "pharmacy",
            "police",
            "post_office",
            "primary_school",
            "restaurant",
            "school",
            "secondary_school",
            "shoe_store",
            "shopping_mall",
            "spa",
            "stadium",
            "store",
            "subway_station",
            "supermarket",
            "synagogue",
            "tourist_attraction",
            "train_station",
            "university",
            "veterinary_care",
            "zoo"
        )
    }
}
