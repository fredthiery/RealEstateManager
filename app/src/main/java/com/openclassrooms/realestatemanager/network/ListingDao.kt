package com.openclassrooms.realestatemanager.network

import android.database.Cursor
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.models.Listing
import com.openclassrooms.realestatemanager.models.ListingFull
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.PointOfInterest
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingDao {
    @Transaction
    @Query("SELECT * FROM listing ORDER BY onSaleDate")
    fun getAll(): Flow<List<ListingFull>>

    @Query("SELECT * FROM listing ORDER BY onSaleDate")
    fun getAllWithCursor(): Cursor

    @Transaction
    @RawQuery
    suspend fun searchListings(query: SupportSQLiteQuery): List<ListingFull>

    @Transaction
    @Query("SELECT * FROM listing WHERE id=:listingId")
    fun getListing(listingId: String): Flow<ListingFull>

    @Query("SELECT * FROM photo WHERE listingId=:listingId")
    fun getPhotos(listingId: String): Flow<List<Photo>>

    @Query("SELECT * FROM photo WHERE id=:id")
    suspend fun getPhoto(id: String?): Photo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(listing: Listing): Long

    @Update
    suspend fun update(listing: Listing)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: Photo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: MutableList<Photo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPOIs(pois: MutableList<PointOfInterest>)

    @Delete
    suspend fun delete(photos: List<Photo>)

}