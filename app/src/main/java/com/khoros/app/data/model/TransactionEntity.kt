package com.khoros.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single income or expense transaction.
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val category: String,
    val amount: Float,
    val date: String,
    val notes: String = ""
)
