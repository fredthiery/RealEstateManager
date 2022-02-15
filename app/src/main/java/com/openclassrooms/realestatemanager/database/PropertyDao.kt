package com.openclassrooms.realestatemanager.database

import androidx.room.*
import com.openclassrooms.realestatemanager.models.Photo
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.PropertyShort
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {
    @Query("SELECT * FROM property")
    fun getAll(): Flow<List<Property>>

    @Query("SELECT * FROM property WHERE id=:propertyId")
    fun getProperty(propertyId: String): Flow<Property>

    @Query("SELECT id,type,price,address FROM property")
    fun getAllShort(): Flow<List<PropertyShort>>

    @Query("SELECT * FROM photo WHERE propertyId=:propertyId")
    fun getPhotos(propertyId: String): Flow<List<Photo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(property: Property)

    @Update
    suspend fun update(property: Property)

    @Insert
    suspend fun insertPhoto(photo: Photo)
}