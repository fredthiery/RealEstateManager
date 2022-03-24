package com.openclassrooms.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.openclassrooms.realestatemanager.models.Listing
import com.openclassrooms.realestatemanager.network.ListingDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ListingContentProvider : ContentProvider() {
    private val authority = "com.openclassrooms.realestatemanager.provider"
    private val tableName: String = Listing::class.java.simpleName

    private val database =
        context?.let { ListingDatabase.getDatabase(it, CoroutineScope(SupervisorJob())) }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db = database?.openHelper?.readableDatabase
        return db?.query(
            SupportSQLiteQueryBuilder
                .builder("listing")
                .selection(selection, selectionArgs)
                .columns(projection)
                .orderBy(sortOrder)
                .create()
        )
    }

    override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.item/$authority.$tableName";
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val db = database?.openHelper?.writableDatabase
        val result = db?.insert("listing", 0, contentValues)
        return result?.let { ContentUris.withAppendedId(uri, it) }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = database?.openHelper?.writableDatabase
        return db?.delete("listing", selection, selectionArgs) ?: 0
    }

    override fun update(
        uri: Uri,
        contentValues: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val db = database?.openHelper?.writableDatabase
        return db?.update("listing",0,contentValues,selection,selectionArgs) ?: 0
    }
}