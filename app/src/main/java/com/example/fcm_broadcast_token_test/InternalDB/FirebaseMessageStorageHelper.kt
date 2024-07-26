package com.example.fcm_broadcast_token_test.InternalDB

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class FirebaseMessageStorageHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "FCM_STORAGE"
        const val TOKEN_TABLE_NAME = "fcm_token_storage"
        const val TOKEN = "token"
        const val DRIVER_NO = "driver_no"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TOKEN_TABLE = """
            CREATE TABLE $TOKEN_TABLE_NAME(
                $TOKEN TEXT PRIMARY KEY,
                $DRIVER_NO INTEGER
            )
        """.trimIndent()
        db.execSQL(CREATE_TOKEN_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TOKEN_TABLE_NAME")
        onCreate(db)
    }

    fun setTokenData(token: String, driverNo: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(TOKEN, token)
            put(DRIVER_NO, driverNo)
        }

        try {
            db.insertWithOnConflict(TOKEN_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE)
            Log.d("FirebaseMessageStorageHelper", "Token inserted successfully: $token")
        } catch (e: Exception) {
            Log.e("FirebaseMessageStorageHelper", "Error inserting token: ${e.message}")
        } finally {
            db.close()
        }
    }

    fun getToken(): String? {
        val db = this.readableDatabase
        val selectQuery = "SELECT $TOKEN FROM $TOKEN_TABLE_NAME LIMIT 1"
        var token: String? = null

        db.rawQuery(selectQuery, null).use { cursor ->
            if (cursor.moveToFirst()) {
                val tokenIndex = cursor.getColumnIndex(TOKEN)
                if (tokenIndex != -1) {
                    token = cursor.getString(tokenIndex)
                }
            }
        }
        db.close()
        return token
    }
}