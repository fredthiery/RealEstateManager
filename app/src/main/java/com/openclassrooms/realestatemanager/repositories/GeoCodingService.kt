package com.openclassrooms.realestatemanager.repositories

import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.models.geoCodingResponse.GeoCodingResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoCodingService {
    @GET("json?key=" + BuildConfig.MAPS_API_KEY)
    suspend fun getPlaceByAddress(@Query("address") address: String?): GeoCodingResponse

    @GET("json?key=" + BuildConfig.MAPS_API_KEY)
    suspend fun getPlaceByLatLng(@Query("latlng") latlng: String?): GeoCodingResponse

    companion object {
        fun create(): GeoCodingService {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/geocode/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(GeoCodingService::class.java)
        }
    }
}