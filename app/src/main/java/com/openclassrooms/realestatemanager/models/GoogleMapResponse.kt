package com.openclassrooms.realestatemanager.models

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class GoogleMapResponse(
    @SerializedName("plus_code") val plusCode: PlusCode? = null,
    val results: List<Place>,
    val status: String
)

data class Place(
    @SerializedName("address_components") val addressComponents: List<AddressComponent>? = null,
    @SerializedName("formatted_address") val formattedAddress: String,
    val geometry: Geometry,
    @SerializedName("place_id") val placeId: String? = null,
    @SerializedName("plus_code") val plusCode: PlusCode? = null,
    val types: List<String> = ArrayList(),
    val name: String = ""
) {
    override fun toString(): String {
        return formattedAddress
    }
    fun toLatLng(): LatLng {
        return LatLng(geometry.location.lat,geometry.location.lng)
    }
}

data class AddressComponent(
    @SerializedName("long_name") val longName: String? = null,
    @SerializedName("short_name") val shortName: String? = null,
    val types: List<String>? = null
)

data class Geometry(
    val location: Location,
    @SerializedName("location_type") val locationType: String
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class PlusCode(
    @SerializedName("compound_code") val compoundCode: String? = null,
    @SerializedName("global_code") val globalCode: String? = null
)