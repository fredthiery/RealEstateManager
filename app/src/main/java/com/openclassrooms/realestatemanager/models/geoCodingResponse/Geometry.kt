package com.openclassrooms.realestatemanager.models.geoCodingResponse

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class Geometry (
    val location: Location,
    @SerializedName("location_type")
    val locationType: String,
    val viewport: Viewport
)