package com.openclassrooms.realestatemanager.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photo(
    @PrimaryKey val id: String,
    val title: String,
    val uri: Uri,
    val propertyId: String
    )
