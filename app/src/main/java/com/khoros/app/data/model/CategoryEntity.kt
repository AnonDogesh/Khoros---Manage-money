package com.khoros.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a category for classifying transactions.
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val iconRes: String,
    val colorHex: String
)
