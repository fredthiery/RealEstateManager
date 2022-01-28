package com.openclassrooms.realestatemanager.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.net.URI
import java.util.*

@Entity
data class Property(
        @PrimaryKey val id: Int,
        val type: String,
        val price: Int,
        val area: Int?,
        val numberOfRooms: Int?,
        val description: String?,
        val photos: List<URI>?,
        val address: String,
        val pointsOfInterest: List<String>?,
        val sellStatus: Boolean,
        val onSaleDate: Calendar,
        val sellDate: Calendar?,
        val realtor: String
)
