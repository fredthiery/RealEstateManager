package com.openclassrooms.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanager.models.Converters
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@Database(entities = [Property::class, Photo::class], version = 1)
@TypeConverters(Converters::class)
abstract class PropertyDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao

    companion object {
        @Volatile
        private var INSTANCE: PropertyDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): PropertyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PropertyDatabase::class.java,
                    "property_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(PropertyDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class PropertyDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.propertyDao())
                }
            }
        }

        suspend fun populateDatabase(propertyDao: PropertyDao) {
            val cal: Calendar = Calendar.getInstance()
            cal.set(2022, 2, 3, 12, 30)
            propertyDao.insert(
                Property(
                    "1",
                    "Appartement",
                    1500000,
                    80,
                    4,
                    "Bel appartement de luxe",
                    "3 place du capitole, New York",
                    false,
                    cal,
                    null,
                    "Fred"
                )
            )
            cal.set(2022, 1, 7, 10, 0)
            propertyDao.insert(
                Property(
                    "2",
                    "Maison",
                    3000000,
                    150,
                    6,
                    "Magnifique maison",
                    "2 rue inconnue, New York",
                    false,
                    cal,
                    null,
                    "Fred"
                )
            )
        }
    }
}