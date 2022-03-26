package com.openclassrooms.realestatemanager.models

import android.content.ContentValues
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
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var type: String = "",
    var price: Int? = null,
    var neighborhood: String = "",
    var area: Int? = null,
    var numberOfRooms: Int? = 1,
    var numberOfBedrooms: Int? = null,
    var numberOfBathrooms: Int? = null,
    var description: String = "",
    var address: String = "",
    var latLng: LatLng? = null,
    var onSaleDate: Calendar = Calendar.getInstance(),
    var sellDate: Calendar? = null,
    var realtor: String? = null,
    var thumbnailId: Long? = null
) : ClusterItem {

    override fun getPosition(): LatLng {
        return latLng ?: LatLng(0.0, 0.0)
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
        if (neighborhood != other.neighborhood) return false
        if (price != other.price) return false
        if (address != other.address) return false
        if (thumbnailId != other.thumbnailId) return false
        if (sellDate != other.sellDate) return false

        return true
    }

    companion object {
        fun fromContentValues(values: ContentValues): Listing {
            val onSaleDate = Calendar.getInstance()
            onSaleDate.timeInMillis = values.getLong("onSaleDate") ?: 0
            val sellDate = Calendar.getInstance()
            sellDate.timeInMillis = values.getLong("sellDate") ?: 0

            return Listing(
                type = values.getString("type") ?: "",
                price = values.getInt("price"),
                neighborhood = values.getString("neighborhood") ?: "",
                area = values.getInt("area"),
                numberOfRooms = values.getInt("numberOfRooms"),
                numberOfBedrooms = values.getInt("numberOfBedrooms"),
                numberOfBathrooms = values.getInt("numberOfBathrooms"),
                description = values.getString("description") ?: "",
                address = values.getString("address") ?: "",
                latLng = LatLng(values.getDouble("lat") ?: 0.0, values.getDouble("lng") ?: 0.0),
                onSaleDate = onSaleDate,
                sellDate = sellDate,
                realtor = values.getString("realtor"),
                thumbnailId = values.getLong("thumbnailId")
            )
        }
    }
}

fun ContentValues.getString(key: String): String? {
    return if (containsKey(key)) getAsString(key)
    else null
}

fun ContentValues.getInt(key: String): Int? {
    return if (containsKey(key)) getAsInteger(key)
    else null
}

fun ContentValues.getDouble(key: String): Double? {
    return if (containsKey(key)) getAsDouble(key)
    else null
}

fun ContentValues.getLong(key: String): Long? {
    return if (containsKey(key)) getAsLong(key)
    else null
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
    fun fromLatLng(latLng: LatLng?): String =
        if (latLng != null) latLng.latitude.toString() + "," + latLng.longitude.toString() else ""

    @TypeConverter
    fun toLatLng(string: String?): LatLng? {
        if (string == "" || string == null) return null
        val strings = string.split(",")
        return LatLng(strings[0].toDouble(), strings[1].toDouble())
    }
}
