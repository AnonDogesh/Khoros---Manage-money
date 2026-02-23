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

    suspend fun addCategory(name: String, iconRes: String = "category", colorHex: String = "#DDA853") {
        categoryDao.insert(CategoryEntity(name = name, iconRes = iconRes, colorHex = colorHex))
    }



    suspend fun resetOfflineData() {
        transactionDao.clearAll()
        categoryDao.clearAll()
        seedCategoriesIfDatabaseEmpty()
    }

    suspend fun deleteOfflineAccountData() {
        transactionDao.clearAll()
        categoryDao.clearAll()
    }

    suspend fun seedCategoriesIfDatabaseEmpty() {
        if (categoryDao.countCategories() > 0) return
        categoryDao.upsert(
            listOf(
                CategoryEntity(name = "Food", iconRes = "restaurant", colorHex = "#DDA853"),
                CategoryEntity(name = "Travel", iconRes = "directions_bus", colorHex = "#27548A"),
                CategoryEntity(name = "Shop", iconRes = "shopping_bag", colorHex = "#183B4E"),
                CategoryEntity(name = "Bills", iconRes = "receipt_long", colorHex = "#27548A"),
                CategoryEntity(name = "Fun", iconRes = "local_movies", colorHex = "#DDA853"),
                CategoryEntity(name = "Health", iconRes = "medical_services", colorHex = "#183B4E"),
                CategoryEntity(name = "Learn", iconRes = "school", colorHex = "#27548A"),
                CategoryEntity(name = "Other", iconRes = "more_horiz", colorHex = "#DDA853")
            )
        )
    }
}
