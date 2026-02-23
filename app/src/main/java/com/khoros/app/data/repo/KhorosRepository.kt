package com.khoros.app.data.repo

import com.khoros.app.data.local.CategoryDao
import com.khoros.app.data.local.TransactionDao
import com.khoros.app.data.model.CategoryEntity
import com.khoros.app.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository that exposes app data as streams and operations.
 */
class KhorosRepository(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) {
    val transactions: Flow<List<TransactionEntity>> = transactionDao.observeTransactions()
    val categories: Flow<List<CategoryEntity>> = categoryDao.observeCategories()

    suspend fun addTransaction(item: TransactionEntity) = transactionDao.insert(item)

    suspend fun updateTransaction(item: TransactionEntity) = transactionDao.update(item)

    suspend fun deleteTransaction(item: TransactionEntity) = transactionDao.delete(item)

    suspend fun deleteTransactionById(id: Int) = transactionDao.deleteById(id)

    suspend fun seedCategoriesIfEmpty(current: List<CategoryEntity>) {
        if (current.isNotEmpty()) return
        categoryDao.upsert(
            listOf(
                CategoryEntity(name = "Food & Dining", iconRes = "restaurant", colorHex = "#BBC863"),
                CategoryEntity(name = "Transport", iconRes = "directions_car", colorHex = "#658C58"),
                CategoryEntity(name = "Utilities", iconRes = "bolt", colorHex = "#31694E"),
                CategoryEntity(name = "Rent & Bills", iconRes = "home", colorHex = "#F0E491")
            )
        )
    }
}
