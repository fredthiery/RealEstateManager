package com.openclassrooms.realestatemanager.database

import androidx.room.*
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.Listing
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingDao {
    @Query("SELECT * FROM listing")
    fun getAll(): Flow<List<Listing>>

    @Query("SELECT * FROM listing WHERE id=:listingId")
    fun getListing(listingId: String): Flow<Listing>

    @Query("SELECT * FROM photo WHERE listingId=:listingId")
    fun getPhotos(listingId: String): Flow<List<Photo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(listing: Listing)

    @Update
    suspend fun update(listing: Listing)

    @Insert
    suspend fun insert(photo: Photo)
}