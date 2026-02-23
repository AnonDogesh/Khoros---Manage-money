package com.khoros.app.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.io.File

/**
 * Stores user settings for theme and preferred currency.
 */
class SettingsViewModel : ViewModel() {
    var selectedCurrency by mutableStateOf("₹")
        private set

    val currencyCode: String
        get() = when (selectedCurrency) {
            "₹" -> "INR (₹)"
            "$" -> "USD ($)"
            "€" -> "EUR (€)"
            else -> selectedCurrency
        }

    var darkThemeEnabled by mutableStateOf(false)
        private set

    fun setCurrency(value: String) {
        selectedCurrency = value
    }

    fun toggleTheme(enabled: Boolean) {
        darkThemeEnabled = enabled
    }

    /**
     * Exports a csv file into app-local files directory and returns its path.
     */
    fun exportCsv(context: Context, rows: List<String>): String {
        val file = File(context.filesDir, "khoros_export.csv")
        file.writeText(rows.joinToString(separator = "\n"))
        return file.absolutePath
    }
}
