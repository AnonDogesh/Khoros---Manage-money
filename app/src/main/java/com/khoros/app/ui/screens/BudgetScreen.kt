package com.khoros.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.khoros.app.viewmodel.TransactionsViewModel

/**
 * Budget screen with limits synced to transaction categories.
 */
@Composable
fun BudgetScreen(
    viewModel: TransactionsViewModel,
    requestAddCategory: Boolean,
    onAddCategoryConsumed: () -> Unit
) {
    val categories = viewModel.categories.value
    val limits = remember { mutableStateMapOf<String, Int>() }
    var showAddDialog by remember { mutableStateOf(false) }

    categories.forEach { category ->
        if (limits[category.name] == null) limits[category.name] = 0
    }

    LaunchedEffect(requestAddCategory) {
        if (requestAddCategory) {
            showAddDialog = true
            onAddCategoryConsumed()
        }
    }

    val totalBudget = limits.values.sum()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Budget & Limits", style = MaterialTheme.typography.headlineSmall)
        Text("Total Budget: ₹$totalBudget", style = MaterialTheme.typography.titleLarge)

        categories.forEach { category ->
            val limit = limits[category.name] ?: 0
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(category.name, style = MaterialTheme.typography.titleLarge)
                        Text("₹$limit")
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { limits[category.name] = (limits[category.name] ?: 0) + 500 }) {
                            Text("Set Limit")
                        }
                        Button(onClick = { limits[category.name] = ((limits[category.name] ?: 0) - 500).coerceAtLeast(0) }) {
                            Text("Reduce")
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBudgetCategoryDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, amount ->
                viewModel.addCategory(name)
                limits[name] = amount
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddBudgetCategoryDialog(onDismiss: () -> Unit, onAdd: (String, Int) -> Unit) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val limit = amount.toIntOrNull() ?: 0
                if (name.isNotBlank()) onAdd(name.trim(), limit)
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("New Budget Category") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Category name") })
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Monthly limit") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    )
}
