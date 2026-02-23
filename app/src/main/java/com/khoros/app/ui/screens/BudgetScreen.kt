package com.khoros.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Budget screen with simple monthly category limits.
 */
@Composable
fun BudgetScreen() {
    val categoryLimits = remember {
        mutableStateListOf(
            "Food & Dining" to 4000,
            "Transport" to 2500,
            "Rent & Bills" to 12000,
            "Groceries" to 3500
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Budget & Limits", style = MaterialTheme.typography.headlineSmall)
        Text("Monthly", style = MaterialTheme.typography.bodyMedium)

        categoryLimits.forEachIndexed { index, (category, limit) ->
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(category, style = MaterialTheme.typography.titleLarge)
                        Text("₹$limit")
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            categoryLimits[index] = category to (limit + 500)
                        }) { Text("Increase") }
                        Button(onClick = {
                            categoryLimits[index] = category to (limit - 500).coerceAtLeast(0)
                        }) { Text("Decrease") }
                    }
                }
            }
        }
    }
}
