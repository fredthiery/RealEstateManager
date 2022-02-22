package com.openclassrooms.realestatemanager.models.geoCodingResponse

import com.google.gson.annotations.SerializedName

data class AddressComponent (
    @SerializedName("long_name")
    val longName: String? = null,
    @SerializedName("short_name")
    val shortName: String? = null,
    val types: List<String>? = null
)