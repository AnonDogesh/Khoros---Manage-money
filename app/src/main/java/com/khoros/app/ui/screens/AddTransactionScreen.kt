package com.khoros.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.khoros.app.data.model.TransactionEntity
import com.khoros.app.viewmodel.TransactionsViewModel
import java.time.LocalDate

/**
 * Full page for adding or editing a transaction.
 */
@Composable
fun AddTransactionScreen(
    viewModel: TransactionsViewModel,
    existing: TransactionEntity?,
    onCancel: () -> Unit,
    onSaved: () -> Unit
) {
    var type by remember(existing?.id) { mutableStateOf(existing?.type ?: "Expense") }
    var amount by remember(existing?.id) { mutableStateOf(existing?.amount?.toString() ?: "") }
    var category by remember(existing?.id) { mutableStateOf(existing?.category ?: "") }
    var date by remember(existing?.id) { mutableStateOf(existing?.date ?: LocalDate.now().toString()) }
    var notes by remember(existing?.id) { mutableStateOf(existing?.notes ?: "") }

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onCancel) { Text("Cancel") }
            Text(if (existing == null) "Add New Transaction" else "Edit Transaction", style = MaterialTheme.typography.titleLarge)
            Button(onClick = {
                type = "Expense"
                amount = ""
                category = ""
                date = LocalDate.now().toString()
                notes = ""
            }) { Text("Reset") }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = type == "Expense", onClick = { type = "Expense" }, label = { Text("Expense") })
            FilterChip(selected = type == "Income", onClick = { type = "Income" }, label = { Text("Income") })
        }

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Text("Select category", style = MaterialTheme.typography.titleLarge)
        viewModel.categories.value.chunked(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { c ->
                    FilterChip(
                        selected = category == c.name,
                        onClick = { category = c.name },
                        label = { Text(c.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Transaction date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = {
                val payload = TransactionEntity(
                    id = existing?.id ?: 0,
                    type = type,
                    category = category.ifBlank { "Other" },
                    amount = amount.toFloatOrNull() ?: 0f,
                    date = date,
                    notes = notes
                )
                if (existing == null) viewModel.add(payload) else viewModel.update(payload)
                onSaved()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Transaction")
        }
    }
}
