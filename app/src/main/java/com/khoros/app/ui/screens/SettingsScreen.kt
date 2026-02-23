package com.khoros.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.khoros.app.data.model.TransactionEntity
import com.khoros.app.viewmodel.SettingsViewModel

/**
 * Shows user preferences and local export action.
 */
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, transactions: List<TransactionEntity>) {
    val context = LocalContext.current
    Column(
        Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Currency", style = MaterialTheme.typography.titleLarge)
                listOf("₹", "$", "€").forEach { currency ->
                    Button(onClick = { viewModel.setCurrency(currency) }, modifier = Modifier.fillMaxWidth()) { Text(currency) }
                }
            }
        }
        Card {
            Column(Modifier.padding(16.dp)) {
                Text("Dark theme")
                Switch(checked = viewModel.darkThemeEnabled, onCheckedChange = viewModel::toggleTheme)
            }
        }
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Export data")
                Button(
                    onClick = {
                        val rows = mutableListOf("id,type,category,amount,date,notes")
                        rows += transactions.map { "${it.id},${it.type},${it.category},${it.amount},${it.date},${it.notes}" }
                        val path = viewModel.exportCsv(context, rows)
                        Toast.makeText(context, "CSV exported: $path", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export CSV")
                }
            }
        }
    }
}
