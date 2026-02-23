package com.khoros.app

import com.khoros.app.data.local.CategoryDao
import com.khoros.app.data.local.TransactionDao
import com.khoros.app.data.model.CategoryEntity
import com.khoros.app.data.model.TransactionEntity
import com.khoros.app.data.repo.KhorosRepository
import com.khoros.app.viewmodel.DashboardViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class DashboardViewModelTest {
    @Test
    fun computesIncomeExpenseAndBalance() = runBlocking {
        val txFlow = MutableStateFlow(
            listOf(
                TransactionEntity(type = "Income", category = "Salary", amount = 1000f, date = "2026-01-01"),
                TransactionEntity(type = "Expense", category = "Food", amount = 250f, date = "2026-01-02")
            )
        )
        val repo = KhorosRepository(
            transactionDao = object : TransactionDao {
                override fun observeTransactions(): Flow<List<TransactionEntity>> = txFlow
                override suspend fun insert(transaction: TransactionEntity) {}
                override suspend fun update(transaction: TransactionEntity) {}
                override suspend fun delete(transaction: TransactionEntity) {}
                override suspend fun deleteById(id: Int) {}
            },
            categoryDao = object : CategoryDao {
                override fun observeCategories(): Flow<List<CategoryEntity>> = MutableStateFlow(emptyList())
                override suspend fun upsert(categories: List<CategoryEntity>) {}
            }
        )
        val vm = DashboardViewModel(repo)
        assertEquals(1000f, vm.totalIncome.first(), 0.01f)
        assertEquals(250f, vm.totalExpense.first(), 0.01f)
        assertEquals(750f, vm.balance.first(), 0.01f)
    }
}
