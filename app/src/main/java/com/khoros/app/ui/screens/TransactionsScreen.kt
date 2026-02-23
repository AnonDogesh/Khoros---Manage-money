package com.khoros.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.LocalMovies
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khoros.app.data.model.CategoryEntity
import com.khoros.app.data.model.TransactionEntity
import com.khoros.app.viewmodel.TransactionsViewModel
import java.time.LocalDate

/**
 * Displays searchable and filterable transactions list.
 */
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

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
                Text("Transactions", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }

        item {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Search transactions...") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    CategoryFilterChip(
                        title = "All",
                        selected = selectedCategory == null,
                        icon = Icons.Rounded.Check,
                        onClick = { selectedCategory = null }
                    )
                }
                items(categories, key = { it.name }) { category ->
                    CategoryFilterChip(
                        title = category.name,
                        selected = selectedCategory == category.name,
                        icon = iconForCategory(category.name),
                        onClick = { selectedCategory = category.name }
                    )
                }
            }
        }

        if (data.isEmpty()) {
            item {
                Text("No transactions yet", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 18.dp))
            }
        } else {
            val today = LocalDate.now().toString()
            val (todayItems, olderItems) = data.partition { it.date == today }

            if (todayItems.isNotEmpty()) {
                item { SectionTitle("TODAY") }
                items(todayItems, key = { it.id }) { item ->
                    TransactionCard(item = item, onEdit = { onEdit(item) }, onDelete = { pendingDelete = item })
                }
            }
            if (olderItems.isNotEmpty()) {
                item { SectionTitle("OLDER") }
                items(olderItems, key = { it.id }) { item ->
                    TransactionCard(item = item, onEdit = { onEdit(item) }, onDelete = { pendingDelete = item })
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

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
}

@Composable
private fun CategoryFilterChip(
    title: String,
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .background(
                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                RoundedCornerShape(50)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
        Text(title, color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun TransactionCard(item: TransactionEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(iconForCategory(item.category), contentDescription = item.category)
            }
            Column {
                Text(item.notes.ifBlank { item.category }, style = MaterialTheme.typography.titleLarge)
                Text("${item.category} • ${item.date}", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                if (item.type == "Income") "+₹${item.amount}" else "-₹${item.amount}",
                style = MaterialTheme.typography.titleLarge,
                color = if (item.type == "Income") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Rounded.Delete, contentDescription = "Delete")
                }
            }
        }
    }
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
