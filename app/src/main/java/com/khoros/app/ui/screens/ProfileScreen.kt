package com.khoros.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.khoros.app.data.model.TransactionEntity
import com.khoros.app.viewmodel.SettingsViewModel

/**
 * Profile screen with account-related actions.
 */
@Composable
fun ProfileScreen(viewModel: SettingsViewModel, transactions: List<TransactionEntity>) {
    val context = LocalContext.current
    var confirmLogout by remember { mutableStateOf(false) }

    Column(
        Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Arunav Das", style = MaterialTheme.typography.headlineSmall)
        Text("• Offline account", style = MaterialTheme.typography.bodyMedium)

        Text("General", style = MaterialTheme.typography.titleLarge)
        ProfileItem("Account Settings") {
            Toast.makeText(context, "Account settings opened", Toast.LENGTH_SHORT).show()
        }
        ProfileItem("Data Privacy") {
            Toast.makeText(context, "Local storage only", Toast.LENGTH_SHORT).show()
        }
        ProfileItem("Export Data") {
            val rows = mutableListOf("id,type,category,amount,date,notes")
            rows += transactions.map { "${it.id},${it.type},${it.category},${it.amount},${it.date},${it.notes}" }
            val path = viewModel.exportCsv(context, rows)
            Toast.makeText(context, "Exported: $path", Toast.LENGTH_LONG).show()
        }

        Text("Preferences", style = MaterialTheme.typography.titleLarge)
        ProfileItem("Categories") {
            Toast.makeText(context, "Category manager opened", Toast.LENGTH_SHORT).show()
        }
        ProfileItem("Currency Settings (${viewModel.selectedCurrency})") {
            val next = when (viewModel.selectedCurrency) {
                "₹" -> "$"
                "$" -> "€"
                else -> "₹"
            }
            viewModel.setCurrency(next)
            Toast.makeText(context, "Currency changed to $next", Toast.LENGTH_SHORT).show()
        }

        TextButton(onClick = { confirmLogout = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Log Out", style = MaterialTheme.typography.titleLarge)
        }
    }

    if (confirmLogout) {
        AlertDialog(
            onDismissRequest = { confirmLogout = false },
            confirmButton = {
                TextButton(onClick = {
                    confirmLogout = false
                    Toast.makeText(context, "Logged out (offline session)", Toast.LENGTH_SHORT).show()
                }) { Text("Confirm") }
            },
            dismissButton = { TextButton(onClick = { confirmLogout = false }) { Text("Cancel") } },
            title = { Text("Log out?") },
            text = { Text("You can sign back in anytime.") }
        )
    }
}

@Composable
private fun ProfileItem(title: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        IconButton(onClick = onClick) {
            Icon(Icons.Rounded.ChevronRight, contentDescription = title)
        }
    }
}
