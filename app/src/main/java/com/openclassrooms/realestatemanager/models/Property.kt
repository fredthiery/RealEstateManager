package com.openclassrooms.realestatemanager.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*

@Entity
data class Property(
        @PrimaryKey val id: String,
        var type: String,
        var price: Int,
        var area: Int? = 0,
        var numberOfRooms: Int? = 1,
        var description: String? = null,
        var address: String,
        var sellStatus: Boolean,
        var onSaleDate: Calendar,
        var sellDate: Calendar? = null,
        var realtor: String? = null
)

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
}
