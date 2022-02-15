package com.openclassrooms.realestatemanager.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PointOfInterest(
    @PrimaryKey val id: String,
    val name: String,
    val latitude: Long,
    val longitude: Long
)
