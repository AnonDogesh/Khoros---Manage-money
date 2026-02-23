package com.khoros.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.khoros.app.data.model.TransactionEntity
import com.khoros.app.viewmodel.TransactionsViewModel

/**
 * Displays searchable and filterable transactions list with swipe actions.
 */
@Composable
fun TransactionsScreen(viewModel: TransactionsViewModel, onEdit: (TransactionEntity) -> Unit) {
    var search by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val data = viewModel.filtered(search, selectedCategory)
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = search, onValueChange = { search = it }, label = { Text("Search") }, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            viewModel.categories.value.take(4).forEach { category ->
                AssistChip(onClick = { selectedCategory = category.name }, label = { Text(category.name) })
            }
        }
        if (data.isEmpty()) Text("No transactions yet", style = MaterialTheme.typography.titleLarge)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(data, key = { it.id }) { item ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = {
                        when (it) {
                            SwipeToDismissBoxValue.StartToEnd -> onEdit(item)
                            SwipeToDismissBoxValue.EndToStart -> viewModel.delete(item)
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
                    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(item.category)
                            Text("${item.date} • ${item.notes}", style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            text = if (item.type == "Income") "+₹${item.amount}" else "-₹${item.amount}",
                            color = if (item.type == "Income") Color(0xFF2E7D32) else MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}
