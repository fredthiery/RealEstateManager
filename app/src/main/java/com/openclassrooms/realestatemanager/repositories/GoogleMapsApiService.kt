package com.openclassrooms.realestatemanager.repositories

import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.models.GoogleMapResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleMapsApiService {
    @GET("geocode/json?key=" + BuildConfig.MAPS_API_KEY)
    suspend fun getPlaceByAddress(@Query("address") address: String?): GoogleMapResponse

    @GET("geocode/json?key=" + BuildConfig.MAPS_API_KEY)
    suspend fun getPlaceByLatLng(@Query("latlng") latlng: String?): GoogleMapResponse

    @GET("place/nearbysearch/json?key=" + BuildConfig.MAPS_API_KEY + "&radius=1000")
    suspend fun getNearbyPOIs(@Query("location") latlng: String?): GoogleMapResponse

    companion object {
        fun create(): GoogleMapsApiService {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(GoogleMapsApiService::class.java)
        }
    }
}