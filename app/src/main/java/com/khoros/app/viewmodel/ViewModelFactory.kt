package com.khoros.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khoros.app.data.repo.KhorosRepository

/**
 * Creates screen view models that depend on the shared repository.
 */
class ViewModelFactory(private val repository: KhorosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> DashboardViewModel(repository) as T
            modelClass.isAssignableFrom(TransactionsViewModel::class.java) -> TransactionsViewModel(repository) as T
            modelClass.isAssignableFrom(AnalyticsViewModel::class.java) -> AnalyticsViewModel(repository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel() as T
            else -> throw IllegalArgumentException("Unknown model class: ${modelClass.name}")
        }
    }
}
