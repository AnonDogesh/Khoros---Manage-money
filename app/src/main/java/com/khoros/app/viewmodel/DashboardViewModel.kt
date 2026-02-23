package com.khoros.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khoros.app.data.model.TransactionEntity
import com.khoros.app.data.repo.KhorosRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Computes dashboard aggregates such as balance, income, and expenses.
 */
class DashboardViewModel(repository: KhorosRepository) : ViewModel() {
    val transactions = repository.transactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncome = transactions.map { items -> items.filter { it.type == "Income" }.sumOf { it.amount.toDouble() }.toFloat() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val totalExpense = transactions.map { items -> items.filter { it.type == "Expense" }.sumOf { it.amount.toDouble() }.toFloat() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val balance = combine(totalIncome, totalExpense) { income, expense -> income - expense }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    /**
     * Creates daily totals for line graph drawing.
     */
    fun dailyExpenseTrend(items: List<TransactionEntity>): List<Pair<Float, Float>> {
        val grouped = items.filter { it.type == "Expense" }
            .groupBy { it.date.takeLast(2).toIntOrNull() ?: 1 }
            .toSortedMap()
        return grouped.map { (day, tx) -> day.toFloat() to tx.sumOf { it.amount.toDouble() }.toFloat() }
    }
}
