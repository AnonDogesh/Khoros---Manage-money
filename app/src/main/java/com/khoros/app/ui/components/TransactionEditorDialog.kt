package com.khoros.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.khoros.app.data.model.CategoryEntity
import com.khoros.app.data.model.TransactionEntity
import java.time.LocalDate

/**
 * Modal dialog used for adding or editing a transaction.
 */
@Composable
fun TransactionEditorDialog(
    categories: List<CategoryEntity>,
    existing: TransactionEntity? = null,
    onDismiss: () -> Unit,
    onSave: (TransactionEntity) -> Unit
) {
    var type by remember { mutableStateOf(existing?.type ?: "Expense") }
    var amount by remember { mutableStateOf(existing?.amount?.toString() ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: categories.firstOrNull()?.name.orEmpty()) }
    var date by remember { mutableStateOf(existing?.date ?: LocalDate.now().toString()) }
    var notes by remember { mutableStateOf(existing?.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                onSave(
                    TransactionEntity(
                        id = existing?.id ?: 0,
                        type = type,
                        category = category,
                        amount = amount.toFloatOrNull() ?: 0f,
                        date = date,
                        notes = notes
                    )
                )
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        text = {
            Column(modifier = Modifier.fillMaxWidth().padding(6.dp)) {
                Text("Add / Edit Transaction")
                FilterChip(selected = type == "Income", onClick = { type = "Income" }, label = { Text("Income") })
                FilterChip(selected = type == "Expense", onClick = { type = "Expense" }, label = { Text("Expense") })
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = category, onValueChange = { category = it }, label = { Text("Category") })
                OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = date, onValueChange = { date = it }, label = { Text("Date (YYYY-MM-DD)") })
                OutlinedTextField(modifier = Modifier.fillMaxWidth(), value = notes, onValueChange = { notes = it }, label = { Text("Notes") })
            }
        }
    )
}
