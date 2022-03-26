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
    @ColumnInfo(index = true) val listingId: Long
)
