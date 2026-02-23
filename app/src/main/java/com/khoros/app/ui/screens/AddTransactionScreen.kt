package com.khoros.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.LocalMovies
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.khoros.app.data.model.CategoryEntity
import com.khoros.app.data.model.TransactionEntity
import com.khoros.app.viewmodel.TransactionsViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Full page for adding or editing a transaction.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: TransactionsViewModel,
    existing: TransactionEntity?,
    onCancel: () -> Unit,
    onSaved: () -> Unit
) {
    var type by remember(existing?.id) { mutableStateOf(existing?.type ?: "Expense") }
    var amount by remember(existing?.id) { mutableStateOf(existing?.amount?.toString() ?: "") }
    var category by remember(existing?.id) { mutableStateOf(existing?.category ?: "Food") }
    var date by remember(existing?.id) { mutableStateOf(existing?.date ?: LocalDate.now().toString()) }
    var notes by remember(existing?.id) { mutableStateOf(existing?.notes ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showManageDialog by remember { mutableStateOf(false) }

    val categories = if (viewModel.categories.value.isEmpty()) {
        listOf(
            CategoryEntity(name = "Food", iconRes = "restaurant", colorHex = "#DDA853"),
            CategoryEntity(name = "Travel", iconRes = "directions_bus", colorHex = "#27548A"),
            CategoryEntity(name = "Shop", iconRes = "shopping_bag", colorHex = "#183B4E"),
            CategoryEntity(name = "Bills", iconRes = "receipt_long", colorHex = "#27548A"),
            CategoryEntity(name = "Fun", iconRes = "local_movies", colorHex = "#DDA853"),
            CategoryEntity(name = "Health", iconRes = "medical_services", colorHex = "#183B4E"),
            CategoryEntity(name = "Learn", iconRes = "school", colorHex = "#27548A"),
            CategoryEntity(name = "Other", iconRes = "more_horiz", colorHex = "#DDA853")
        )
    } else viewModel.categories.value

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onCancel) { Text("Cancel") }
            Text(if (existing == null) "Add New Transaction" else "Edit Transaction", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = {
                type = "Expense"
                amount = ""
                category = "Food"
                date = LocalDate.now().toString()
                notes = ""
            }) { Text("Reset") }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            FilterChip(selected = type == "Expense", onClick = { type = "Expense" }, label = { Text("Expense") }, modifier = Modifier.weight(1f))
            FilterChip(selected = type == "Income", onClick = { type = "Income" }, label = { Text("Income") }, modifier = Modifier.weight(1f))
        }

        Text("Amount", style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("₹", style = MaterialTheme.typography.headlineSmall)
            Text(amount.ifBlank { "0" }, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it.filter { ch -> ch.isDigit() || ch == '.' } },
            label = { Text("Enter amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Select Category", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { showManageDialog = true }) { Text("Manage") }
        }

        categories.chunked(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { c ->
                    CategoryTile(
                        category = c,
                        selected = category == c.name,
                        onClick = { category = c.name },
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(4 - row.size) {
                    SpacerTile(Modifier.weight(1f))
                }
            }
        }

        DateRow(date = date, onClick = { showDatePicker = true })

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes") },
            placeholder = { Text("Add details...") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val payload = TransactionEntity(
                    id = existing?.id ?: 0,
                    type = type,
                    category = category,
                    amount = amount.toFloatOrNull() ?: 0f,
                    date = date,
                    notes = notes
                )
                if (existing == null) viewModel.add(payload) else viewModel.update(payload)
                onSaved()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Save Transaction")
        }
    }

    if (showDatePicker) {
        DatePickerDialogContent(
            initialDate = date,
            onDismiss = { showDatePicker = false },
            onDateSelected = {
                date = it
                showDatePicker = false
            }
        )
    }

    if (showManageDialog) {
        ManageCategoryDialog(
            onDismiss = { showManageDialog = false },
            onAdd = {
                viewModel.addCategory(it)
                category = it
                showManageDialog = false
            }
        )
    }
}

@Composable
private fun DateRow(date: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.CalendarMonth, contentDescription = null)
            Column {
                Text("Transaction Date", style = MaterialTheme.typography.bodyMedium)
                Text(date, style = MaterialTheme.typography.titleLarge)
            }
        }
        Icon(Icons.Rounded.CalendarMonth, contentDescription = null)
    }
}

@Composable
private fun CategoryTile(
    category: CategoryEntity,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = iconForCategory(category.name)
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    if (selected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(16.dp)
                )
                .border(2.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = category.name)
        }
        Text(category.name, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun SpacerTile(modifier: Modifier = Modifier) {
    Box(modifier.height(64.dp).width(1.dp))
}

private fun iconForCategory(name: String): ImageVector {
    return when (name.lowercase()) {
        "food", "food & dining" -> Icons.Rounded.Restaurant
        "travel", "transport" -> Icons.Rounded.DirectionsBus
        "shop" -> Icons.Rounded.ShoppingBag
        "bills", "rent & bills" -> Icons.Rounded.ReceiptLong
        "fun" -> Icons.Rounded.LocalMovies
        "health" -> Icons.Rounded.HealthAndSafety
        "learn" -> Icons.Rounded.School
        else -> Icons.Rounded.MoreHoriz
    }
}

@Composable
private fun ManageCategoryDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var value by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { if (value.isNotBlank()) onAdd(value.trim()) }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Manage Categories") },
        text = {
            OutlinedTextField(value = value, onValueChange = { value = it }, label = { Text("New category") })
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialogContent(initialDate: String, onDismiss: () -> Unit, onDateSelected: (String) -> Unit) {
    val dateMillis = remember(initialDate) {
        runCatching { LocalDate.parse(initialDate).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() }
            .getOrElse { System.currentTimeMillis() }
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val millis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                val localDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                onDateSelected(localDate.toString())
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    ) {
        DatePicker(state = datePickerState)
    }
}
