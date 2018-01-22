package com.alexlytvynenko.appanalyzer.internal

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.alexlytvynenko.appanalyzer.internal.entity.LogEntity
import com.alexlytvynenko.appanalyzer.internal.entity.RequestEntity
import org.jetbrains.anko.db.*

/**
 * Created by alex_litvinenko on 11.10.17.
 */
internal class DatabaseHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "AppAnalyzerDatabase", null, 1) {
    companion object {
        private var instance: DatabaseHelper? = null

        @Synchronized
        fun getInstance(context: Context): DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(database: SQLiteDatabase) {
        database.createTable(RequestEntity.TABLE_NAME, true,
                RequestEntity.COLUMN_METHOD to TEXT, RequestEntity.COLUMN_ID to REAL + PRIMARY_KEY,
                RequestEntity.COLUMN_START_DATE to REAL, RequestEntity.COLUMN_URL to TEXT,
                RequestEntity.COLUMN_REQUEST_HEADERS to BLOB, RequestEntity.COLUMN_REQUEST_BODY to TEXT,
                RequestEntity.COLUMN_STATUS to TEXT, RequestEntity.COLUMN_DURATION to TEXT,
                RequestEntity.COLUMN_RESPONSE_HEADERS to BLOB, RequestEntity.COLUMN_RESPONSE_BODY to TEXT)
        database.createTable(LogEntity.LOG_TABLE_NAME, true,
                LogEntity.COLUMN_ID to REAL + PRIMARY_KEY, LogEntity.COLUMN_START_DATE to REAL,
                LogEntity.COLUMN_PROCESS_THREAD to TEXT, LogEntity.COLUMN_PRIORITY to TEXT,
                LogEntity.COLUMN_TAG to TEXT, LogEntity.COLUMN_TEXT to TEXT)
        database.createTable(LogEntity.EXCEPTION_TABLE_NAME, true,
                LogEntity.COLUMN_ID to REAL + PRIMARY_KEY, LogEntity.COLUMN_START_DATE to REAL,
                LogEntity.COLUMN_PROCESS_THREAD to TEXT, LogEntity.COLUMN_PRIORITY to TEXT,
                LogEntity.COLUMN_TAG to TEXT, LogEntity.COLUMN_TEXT to TEXT)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        database.dropTable(RequestEntity.TABLE_NAME, true)
        database.dropTable(LogEntity.LOG_TABLE_NAME, true)
        database.dropTable(LogEntity.EXCEPTION_TABLE_NAME, true)
    }
}

// Access property for Context
internal val Context.database: DatabaseHelper
    get() = DatabaseHelper.getInstance(applicationContext)