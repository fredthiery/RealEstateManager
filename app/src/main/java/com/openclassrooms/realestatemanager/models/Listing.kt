package com.openclassrooms.realestatemanager.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.util.*

@Entity
data class Listing(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    var type: String = "",
    var price: Int? = null,
    var neighborhood: String = "",
    var area: Int? = null,
    var numberOfRooms: Int? = 1,
    var numberOfBedrooms: Int? = null,
    var numberOfBathrooms: Int? = null,
    var description: String = "",
    var address: String = "",
    var latLng: LatLng = LatLng(0.0, 0.0),
    var sellStatus: Boolean = false,
    var onSaleDate: Calendar = Calendar.getInstance(),
    var sellDate: Calendar? = null,
    var realtor: String? = null,
    var thumbnailId: String? = null
) : ClusterItem {
    @Ignore
    var thumbnail: Photo? = null

    override fun getPosition(): LatLng {
        return latLng
    }

    override fun getTitle(): String {
        return type
    }

    override fun getSnippet(): String {
        return description
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Listing) return false

        if (id != other.id) return false
        if (type != other.type) return false
        if (price != other.price) return false
        if (address != other.address) return false
        if (thumbnail != other.thumbnail) return false

        return true
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Calendar? {
        val cal = Calendar.getInstance()
        return value?.let {
            cal.timeInMillis = it
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
    fun fromLatLng(latLng: LatLng): String =
        latLng.latitude.toString() + "," + latLng.longitude.toString()

    @TypeConverter
    fun toLatLng(string: String): LatLng {
        val strings = string.split(",")
        return LatLng(strings[0].toDouble(), strings[1].toDouble())
    }
}
