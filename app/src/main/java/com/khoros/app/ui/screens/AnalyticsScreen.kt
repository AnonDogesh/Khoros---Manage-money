package com.khoros.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.khoros.app.ui.components.DonutChart
import com.khoros.app.ui.components.LineTrendChart
import com.khoros.app.viewmodel.AnalyticsViewModel
import java.time.LocalDate

/**
 * Presents category spending and trend analytics.
 */
@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel) {
    val tx by viewModel.transactions.collectAsState()
    var mode by remember { mutableStateOf("Overview") }
    var monthFilter by remember { mutableStateOf(LocalDate.now().monthValue.toString().padStart(2, '0')) }
    var showAll by remember { mutableStateOf(false) }

    val monthExpenses = tx.filter {
        it.type == "Expense" && it.date.split("-").getOrNull(1) == monthFilter
    }

    val expenseByCategory = monthExpenses
        .groupBy { it.category }
        .mapValues { (_, list) -> list.sumOf { it.amount.toDouble() }.toFloat() }
        .toList()
        .sortedByDescending { it.second }

    val trend = when (mode) {
        "Daily" -> groupDaily(monthExpenses)
        "Weekly" -> groupWeekly(monthExpenses)
        else -> groupWeekly(monthExpenses)
    }

    val totalSpent = expenseByCategory.sumOf { it.second.toDouble() }.toFloat()

    Column(
        Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Spending Analytics", style = MaterialTheme.typography.headlineSmall)
                Text("Insights & Trends", style = MaterialTheme.typography.bodyMedium)
            }
            OutlinedTextField(
                value = monthFilter,
                onValueChange = { if (it.length <= 2) monthFilter = it },
                label = { Text("Month") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(0.35f)
            )
        }

        DonutChart(expenseByCategory, modifier = Modifier.fillMaxWidth())
        Text("Total spent: ₹${"%.0f".format(totalSpent)}", style = MaterialTheme.typography.titleLarge)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Overview", "Weekly", "Daily").forEach { option ->
                FilterChip(selected = mode == option, onClick = { mode = option }, label = { Text(option) })
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Top Spending", style = MaterialTheme.typography.titleLarge)
                Text(
                    if (showAll) "Collapse" else "View all",
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            val listToShow = if (showAll) expenseByCategory else expenseByCategory.take(3)
            if (expenseByCategory.isEmpty()) {
                Text("No expenses in selected month")
            } else {
                listToShow.forEach { (name, amount) ->
                    val percent = ((amount / totalSpent.coerceAtLeast(1f)) * 100).toInt()
                    TopSpendingItem(name = name, amount = amount, percent = percent)
                }
            }
            Text(
                if (showAll) "Tap here to collapse" else "Tap here to view all",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                    .clickable { showAll = !showAll }
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Spending Trend", style = MaterialTheme.typography.titleLarge)
            LineTrendChart(points = trend)
        }
    }
}

private fun groupDaily(items: List<com.khoros.app.data.model.TransactionEntity>): List<Pair<Float, Float>> {
    return items.groupBy { it.date.takeLast(2).toIntOrNull() ?: 1 }
        .toSortedMap().map { (day, list) -> day.toFloat() to list.sumOf { it.amount.toDouble() }.toFloat() }
}

private fun groupWeekly(items: List<com.khoros.app.data.model.TransactionEntity>): List<Pair<Float, Float>> {
    val grouped = items.groupBy {
        val day = it.date.takeLast(2).toIntOrNull() ?: 1
        ((day - 1) / 7) + 1
    }.toSortedMap()
    return grouped.map { (week, list) -> week.toFloat() to list.sumOf { it.amount.toDouble() }.toFloat() }
}

@Composable
private fun TopSpendingItem(name: String, amount: Float, percent: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name)
            Text("₹${"%.0f".format(amount)}")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((percent / 100f).coerceIn(0f, 1f))
                    .height(8.dp)
                    .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(8.dp))
            )
        }
        Text("$percent%")
    }
}
