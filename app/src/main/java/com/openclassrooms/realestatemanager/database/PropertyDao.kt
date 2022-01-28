package com.openclassrooms.realestatemanager.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {
    @Query("SELECT * FROM Property")
    fun getAll(): Flow<List<Property>>

    @Insert
    fun insert(property: Property)

    @Update
    fun update(property: Property)
}