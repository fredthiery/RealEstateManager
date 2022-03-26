package com.openclassrooms.realestatemanager.models

import android.net.Uri
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
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
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var title: String = "",
    val uri: Uri? = null,
    @ColumnInfo(index = true) val listingId: Long
)