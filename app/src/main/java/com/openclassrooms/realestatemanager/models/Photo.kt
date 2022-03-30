package com.openclassrooms.realestatemanager.models

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
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
    @PrimaryKey(autoGenerate = true) val id: Long = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE,
    var title: String = "",
    val uri: Uri? = null,
    @ColumnInfo(index = true) var listingId: Long
)