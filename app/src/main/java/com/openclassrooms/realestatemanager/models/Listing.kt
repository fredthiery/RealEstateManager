package com.openclassrooms.realestatemanager.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.util.*

@Entity
data class Listing (
        @PrimaryKey val id: String,
        var type: String,
        var price: Int,
        var area: Int? = 0,
        var numberOfRooms: Int? = 1,
        var description: String? = null,
        var address: String,
        var latLng: LatLng,
        var sellStatus: Boolean,
        var onSaleDate: Calendar,
        var sellDate: Calendar? = null,
        var realtor: String? = null,
        var thumbnail: Uri? = null
) : ClusterItem {

    override fun getPosition(): LatLng {
                return latLng
        }

        override fun getTitle(): String {
                return type
        }

        override fun getSnippet(): String? {
                return description
        }
}

class Converters {
        @TypeConverter
        fun fromTimestamp(value: Long?): Calendar? {
                val cal = Calendar.getInstance()
                return value?.let{
                        cal.timeInMillis=it
                        cal
                }
        }
        @TypeConverter
        fun toTimestamp(cal: Calendar?): Long? = cal?.timeInMillis
        @TypeConverter
        fun fromUri(uri: Uri?): String = uri.toString()
        @TypeConverter
        fun toUri(string: String?): Uri = Uri.parse(string)
        @TypeConverter
        fun fromLatLng(latLng: LatLng): String = latLng.latitude.toString() + "," + latLng.longitude.toString()
        @TypeConverter
        fun toLatLng(string: String): LatLng {
                val strings = string.split(",")
                return LatLng(strings[0].toDouble(),strings[1].toDouble())
        }
}
