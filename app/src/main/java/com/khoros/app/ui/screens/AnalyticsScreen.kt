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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.khoros.app.ui.components.DonutChart
import com.khoros.app.ui.components.LineTrendChart
import com.khoros.app.viewmodel.AnalyticsViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Presents category spending and trend analytics.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: AnalyticsViewModel) {
    val tx by viewModel.transactions.collectAsState()
    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showAll by remember { mutableStateOf(false) }

    val monthExpenses = tx.filter {
        val month = it.date.split("-").getOrNull(1)?.toIntOrNull()
        it.type == "Expense" && month == selectedMonth
    }

    val expenseByCategory = monthExpenses
        .groupBy { it.category }
        .mapValues { (_, list) -> list.sumOf { it.amount.toDouble() }.toFloat() }
        .toList()
        .sortedByDescending { it.second }

    val trend = groupWeekly(monthExpenses)
    val totalSpent = expenseByCategory.sumOf { it.second.toDouble() }.toFloat()
    val legendColors = listOf(
        Color(0xFF0F4561),
        Color(0xFF27548A),
        Color(0xFFDDA853),
        Color(0xFF183B4E)
    )

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
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                    .clickable { showMonthPicker = true }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(monthTitle(selectedMonth))
                androidx.compose.material3.Icon(Icons.Rounded.CalendarMonth, contentDescription = "Select month")
            }
        }

        DonutChart(expenseByCategory, modifier = Modifier.fillMaxWidth())
        Text("Total spent: ₹${"%.0f".format(totalSpent)}", style = MaterialTheme.typography.titleLarge)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            expenseByCategory.take(4).forEachIndexed { index, (name, _) ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(10.dp).background(legendColors[index % legendColors.size], CircleShape))
                    Text(name, style = MaterialTheme.typography.bodySmall)
                }
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
                    if (showAll) "View less" else "View all",
                    modifier = Modifier.clickable { showAll = !showAll },
                    color = MaterialTheme.colorScheme.tertiary
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
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Spending Trend", style = MaterialTheme.typography.titleLarge)
            Text("Expenses only", style = MaterialTheme.typography.bodySmall)
            LineTrendChart(points = trend)
        }
    }

    if (showMonthPicker) {
        val now = LocalDate.now().withMonth(selectedMonth)
        val millis = now.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val pickerState = androidx.compose.material3.rememberDatePickerState(initialSelectedDateMillis = millis)
        DatePickerDialog(
            onDismissRequest = { showMonthPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selected = pickerState.selectedDateMillis ?: millis
                    selectedMonth = Instant.ofEpochMilli(selected).atZone(ZoneId.systemDefault()).toLocalDate().monthValue
                    showMonthPicker = false
                }) { Text("Select") }
            },
            dismissButton = { TextButton(onClick = { showMonthPicker = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

private fun monthTitle(month: Int): String {
    return LocalDate.of(LocalDate.now().year, month.coerceIn(1, 12), 1).month.name.lowercase()
        .replaceFirstChar { it.titlecase() }
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
