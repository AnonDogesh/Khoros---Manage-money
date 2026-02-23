package com.khoros.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.khoros.app.data.model.TransactionEntity
import com.khoros.app.viewmodel.TransactionsViewModel

/**
 * Displays searchable and filterable transactions list with swipe actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onEdit: (TransactionEntity) -> Unit,
    onBack: (() -> Unit)? = null
) {
    var search by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var pendingDelete by remember { mutableStateOf<TransactionEntity?>(null) }

    val data = viewModel.filtered(search, selectedCategory)
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
                Text("Transactions", style = MaterialTheme.typography.headlineSmall)
            }
        }

        OutlinedTextField(value = search, onValueChange = { search = it }, label = { Text("Search transactions") }, modifier = Modifier.fillMaxWidth())
        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { AssistChip(onClick = { selectedCategory = null }, label = { Text("All") }) }
                    items(viewModel.categories.value, key = { it.id }) { category ->
                        AssistChip(onClick = { selectedCategory = category.name }, label = { Text(category.name) })
                    }
                }
            }
            if (data.isEmpty()) {
                item { Text("No transactions yet", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 24.dp)) }
            } else {
                items(data, key = { it.id }) { item ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            when (it) {
                                SwipeToDismissBoxValue.StartToEnd -> onEdit(item)
                                SwipeToDismissBoxValue.EndToStart -> pendingDelete = item
                                SwipeToDismissBoxValue.Settled -> Unit
                            }
                            false
                        }
                    )
                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Icon(Icons.Rounded.Edit, contentDescription = "Edit")
                                Icon(Icons.Rounded.Delete, contentDescription = "Delete")
                            }
                        }
                    ) {
                        Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(item.notes.ifBlank { item.category }, style = MaterialTheme.typography.titleLarge)
                                Text("${item.category} • ${item.date}", style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(if (item.type == "Income") "+₹${item.amount}" else "-₹${item.amount}")
                        }
                    }
                }
            }
        }
    }

    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(pendingDelete!!)
                    pendingDelete = null
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("Cancel") } },
            title = { Text("Delete transaction?") },
            text = { Text("This action cannot be undone.") }
        )
    }
}
