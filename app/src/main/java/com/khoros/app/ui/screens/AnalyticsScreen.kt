package com.khoros.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.khoros.app.ui.components.DonutChart
import com.khoros.app.ui.components.LineTrendChart
import com.khoros.app.viewmodel.AnalyticsViewModel

/**
 * Presents category spending and trend analytics.
 */
@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel) {
    val tx by viewModel.transactions.collectAsState()
    val expenseByCategory = tx.filter { it.type == "Expense" }
        .groupBy { it.category }
        .mapValues { (_, list) -> list.sumOf { it.amount.toDouble() }.toFloat() }
        .toList()
        .sortedByDescending { it.second }
    val trend = tx.filter { it.type == "Expense" }
        .groupBy { it.date.takeLast(2).toIntOrNull() ?: 1 }
        .toSortedMap().map { (day, list) -> day.toFloat() to list.sumOf { it.amount.toDouble() }.toFloat() }

    LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card {
                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("Spending Analytics", style = MaterialTheme.typography.titleLarge)
                    DonutChart(expenseByCategory)
                }
            }
        }
        item {
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("Spending trend", style = MaterialTheme.typography.titleLarge)
                    LineTrendChart(points = trend)
                }
            }
        }
        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Top categories", style = MaterialTheme.typography.titleLarge)
                    expenseByCategory.take(3).forEach { (name, amount) ->
                        val percent = if (expenseByCategory.sumOf { it.second.toDouble() } == 0.0) 0 else ((amount / expenseByCategory.sumOf { it.second.toDouble() }.toFloat()) * 100).toInt()
                        Text("$name: ₹${"%.0f".format(amount)} ($percent%)")
                    }
                }
            }
        }
    }
}
