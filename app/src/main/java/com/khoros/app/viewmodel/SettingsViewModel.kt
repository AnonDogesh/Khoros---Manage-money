package com.khoros.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * Stores user settings for theme and preferred currency.
 */
class SettingsViewModel : ViewModel() {
    var selectedCurrency by mutableStateOf("₹")
        private set

    var darkThemeEnabled by mutableStateOf(false)
        private set

    fun setCurrency(value: String) {
        selectedCurrency = value
    }

    fun toggleTheme(enabled: Boolean) {
        darkThemeEnabled = enabled
    }
}
