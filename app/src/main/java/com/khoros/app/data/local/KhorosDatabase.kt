package com.khoros.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.khoros.app.data.model.CategoryEntity
import com.khoros.app.data.model.TransactionEntity

/**
 * Main Room database holder for Khoros.
 */
@Database(entities = [TransactionEntity::class, CategoryEntity::class], version = 1, exportSchema = false)
abstract class KhorosDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: KhorosDatabase? = null

        /**
         * Returns a singleton database instance.
         */
        fun getInstance(context: Context): KhorosDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    KhorosDatabase::class.java,
                    "khoros.db"
                ).build().also { INSTANCE = it }
            }
    }
}
