package com.khoros.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.khoros.app.data.model.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * Provides read/write operations for categories.
 */
@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: CategoryEntity): Long

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun countCategories(): Int

    @Query("DELETE FROM categories")
    suspend fun clearAll()
}
