package com.openclassrooms.realestatemanager.database

import androidx.room.*
import com.openclassrooms.realestatemanager.models.Listing
import com.openclassrooms.realestatemanager.models.ListingWithPhotos
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.PointOfInterest
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingDao {
    @Query("SELECT * FROM listing ORDER BY onSaleDate")
    fun getAll(): Flow<List<Listing>>

    @Query(
        "SELECT * FROM listing"
                + " WHERE (type LIKE :search or description LIKE :search or address LIKE :search)"
                + " AND (area >= :areaMin) AND (area <= :areaMax)"
                + " AND (price >= :priceMin) AND (price <= :priceMax)"
                + " AND (numberOfRooms >= :roomsMin) AND (numberOfRooms <= :roomsMax)"
                + " ORDER BY onSaleDate"
    )
    fun searchListings(
        search: String,
        areaMin: Int,
        areaMax: Int,
        priceMin: Int,
        priceMax: Int,
        roomsMin: Int,
        roomsMax: Int
    ): Flow<List<Listing>>

    @Transaction
    @Query("SELECT * FROM listing WHERE id=:listingId")
    fun getListing(listingId: String): Flow<ListingWithPhotos>

    @Query("SELECT * FROM photo WHERE listingId=:listingId")
    fun getPhotos(listingId: String): Flow<List<Photo>>

    @Query("SELECT * FROM photo WHERE id=:id")
    suspend fun getPhoto(id: String?): Photo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(listing: Listing)

    @Update
    suspend fun update(listing: Listing)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: Photo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: MutableList<Photo>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPOIs(pois: MutableList<PointOfInterest>)

    @Delete
    suspend fun delete(photo: Photo)

}