package com.openclassrooms.realestatemanager

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.openclassrooms.realestatemanager.network.ListingDatabase
import com.openclassrooms.realestatemanager.provider.ListingContentProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ListingContentProviderTest {

    private lateinit var db: ListingDatabase
    private lateinit var contentResolver: ContentResolver

    @Before
    fun before() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ListingDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        ListingContentProvider.database = db
        contentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
    }

    @Test
    fun getItemsWhenEmpty() {
        val cursor = contentResolver.query(
            ContentUris.withAppendedId(ListingContentProvider.uriItem, 0),
            null,
            null,
            null,
            null
        )
        assertThat(cursor).isNotNull()
        assertThat(cursor?.count).isEqualTo(0)
        cursor?.close()
    }

    @Test
    fun insertAndGetItem() {
        val uri = contentResolver.insert(ListingContentProvider.uriItem, generateItem())
        assertThat(uri).isNotNull()
        val cursor = contentResolver.query(
            ContentUris.withAppendedId(ListingContentProvider.uriItem, 0),
            null,
            null,
            null,
            null
        )
        assertThat(cursor).isNotNull()
        assertThat(cursor?.count).isEqualTo(1)
        assertThat(cursor?.moveToFirst()).isTrue()
        assertThat(cursor?.getString(cursor.getColumnIndexOrThrow("type"))).isEqualTo("House")
    }

    private fun generateItem(): ContentValues {
        val values = ContentValues()
        values.put("id", "1")
        values.put("type", "House")
        values.put("price", 100000)
        values.put("neighborhood", "Brooklyn")
        values.put("description", "Nice little house")
        values.put("address", "Somewhere")
        values.put("onSaleDate", 0)
        return values
    }
}