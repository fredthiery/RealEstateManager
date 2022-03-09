package com.openclassrooms.realestatemanager.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    foreignKeys = [ForeignKey(
        entity = Listing::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("listingId"),
        onDelete = CASCADE
    )]
)
data class Photo(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    var title: String = "",
    val uri: Uri,
    val listingId: String
) {
    @Ignore
    var thumbnail: Boolean = false
}
