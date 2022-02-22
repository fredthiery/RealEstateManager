package com.openclassrooms.realestatemanager.models.geoCodingResponse

import com.google.gson.annotations.SerializedName

data class Place(
    @SerializedName("address_components")
    val addressComponents: List<AddressComponent>? = null,

    @SerializedName("formatted_address")
    val formattedAddress: String,

    val geometry: Geometry,

    @SerializedName("place_id")
    val placeId: String? = null,

    @SerializedName("plus_code")
    val plusCode: PlusCode? = null,

    val types: List<String>? = null
)