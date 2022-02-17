package com.openclassrooms.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.models.Converters
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.Listing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@Database(entities = [Listing::class, Photo::class], version = 1)
@TypeConverters(Converters::class)
abstract class ListingDatabase : RoomDatabase() {
    abstract fun listingDao(): ListingDao

    companion object {
        @Volatile
        private var INSTANCE: ListingDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): ListingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ListingDatabase::class.java,
                    "listing_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(ListingDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class ListingDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.listingDao())
                }
            }
        }

        suspend fun populateDatabase(listingDao: ListingDao) {
            val cal: Calendar = Calendar.getInstance()
            cal.set(2022, 2, 3, 12, 30)
            listingDao.insert(
                Listing(
                    "1",
                    "Appartement",
                    1500000,
                    80,
                    4,
                    "Bel appartement de luxe",
                    "3 place du capitole, New York",
                    LatLng(48.845,2.367),
                    false,
                    cal,
                    null,
                    "Fred"
                )
            )
            cal.set(2022, 1, 7, 10, 0)
            listingDao.insert(
                Listing(
                    "2",
                    "Maison",
                    3000000,
                    150,
                    6,
                    "Magnifique maison",
                    "2 rue inconnue, New York",
                    LatLng(48.640,2.256),
                    false,
                    cal,
                    null,
                    "Fred"
                )
            )
        }
    }
}