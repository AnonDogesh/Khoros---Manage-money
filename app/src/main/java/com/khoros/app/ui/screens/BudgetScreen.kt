package com.khoros.app.ui.screens

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Budget screen with simple monthly category limits.
 */
@Composable
fun BudgetScreen(requestAddCategory: Boolean, onAddCategoryConsumed: () -> Unit) {
    val categoryLimits = remember {
        mutableStateListOf(
            "Food & Dining" to 4000,
            "Transport" to 2500,
            "Rent & Bills" to 12000,
            "Groceries" to 3500
        )
    }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(requestAddCategory) {
        if (requestAddCategory) {
            showAddDialog = true
            onAddCategoryConsumed()
        }
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

    if (showAddDialog) {
        AddBudgetCategoryDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, amount ->
                categoryLimits.add(name to amount)
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
