package com.khoros.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.khoros.app.ui.components.LineTrendChart
import com.khoros.app.viewmodel.DashboardViewModel

/**
 * Shows high-level wallet summary and spending trend.
 */
@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val tx by viewModel.transactions.collectAsState()
    val income by viewModel.totalIncome.collectAsState()
    val expense by viewModel.totalExpense.collectAsState()
    val balance by viewModel.balance.collectAsState()

    LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Total Balance", style = MaterialTheme.typography.titleLarge)
                    Text("₹${"%.2f".format(balance)}", style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard("Income this month", income, Modifier.weight(1f))
                MetricCard("Expenses this month", expense, Modifier.weight(1f))
                MetricCard("Remaining", income - expense, Modifier.weight(1f))
            }
        }
        item {
            Card {
                Column(Modifier.padding(16.dp)) {
                    Text("Daily spending trend", style = MaterialTheme.typography.titleLarge)
                    LineTrendChart(points = viewModel.dailyExpenseTrend(tx))
                }
            }
        }
    }
}

@Composable
private fun MetricCard(title: String, amount: Float, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.bodySmall)
            Text("₹${"%.0f".format(amount)}", style = MaterialTheme.typography.titleLarge)
        }
    }
}
