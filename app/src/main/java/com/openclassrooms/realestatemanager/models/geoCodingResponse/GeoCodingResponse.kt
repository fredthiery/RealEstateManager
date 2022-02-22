package com.openclassrooms.realestatemanager.models.geoCodingResponse

import com.google.gson.annotations.SerializedName

data class GeoCodingResponse(
    @SerializedName("plus_code")
    val plusCode: PlusCode? = null,
    val results: List<Place>,
    val status: String
)