package com.openclassrooms.realestatemanager.fakes

import com.openclassrooms.realestatemanager.models.Geometry
import com.openclassrooms.realestatemanager.models.GoogleMapResponse
import com.openclassrooms.realestatemanager.models.Location
import com.openclassrooms.realestatemanager.models.Place
import com.openclassrooms.realestatemanager.network.GoogleMapsApiService

class ServiceFake : GoogleMapsApiService {
    private val response = GoogleMapResponse(
        results = listOf(
            Place(
                name = "Tour Eiffel",
                formattedAddress = "Champ de Mars, 5 Av. Anatole France, 75007 Paris",
                types = listOf("amusement_park"),
                geometry = Geometry(Location(48.858370, 2.294481))
            )
        ),
        status = "OK"
    )

    override suspend fun getPlaceByAddress(address: String?): GoogleMapResponse {
        return response
    }

    override suspend fun getPlaceByLatLng(latlng: String?): GoogleMapResponse {
        return response
    }

    override suspend fun getNearbyPOIs(latlng: String?): GoogleMapResponse {
        return response
    }
}