package com.khoros.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khoros.app.data.model.TransactionEntity
import com.khoros.app.data.repo.KhorosRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Handles transaction list filtering and CRUD actions.
 */
class TransactionsViewModel(private val repository: KhorosRepository) : ViewModel() {
    val transactions = repository.transactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val categories = repository.categories.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch { repository.seedCategoriesIfEmpty(categories.value) }
    }

    /**
     * Filters transactions by text and optional category.
     */
    fun filtered(search: String, category: String?): List<TransactionEntity> {
        val input = search.trim().lowercase()
        return transactions.value.filter {
            (category == null || it.category == category) &&
                (input.isBlank() || it.category.lowercase().contains(input) || it.notes.lowercase().contains(input))
        }
    }

    /**
     * Returns a transaction by id for edit screens.
     */
    fun findById(id: Int): TransactionEntity? = transactions.value.firstOrNull { it.id == id }

    fun add(item: TransactionEntity) = viewModelScope.launch { repository.addTransaction(item) }

    fun update(item: TransactionEntity) = viewModelScope.launch { repository.updateTransaction(item) }

    fun delete(item: TransactionEntity) = viewModelScope.launch { repository.deleteTransaction(item) }
}
