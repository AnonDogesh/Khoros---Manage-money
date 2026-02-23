package com.khoros.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.SouthWest
import androidx.compose.material.icons.rounded.NorthEast
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khoros.app.data.model.TransactionEntity
import com.khoros.app.viewmodel.SettingsViewModel
import java.time.LocalDate

/**
 * Profile page matching the provided design with fully functional actions.
 */
@Composable
fun ProfileScreen(
    viewModel: SettingsViewModel,
    transactions: List<TransactionEntity>,
    onOpenAccountSettings: () -> Unit,
    onOpenCategories: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var confirmLogout by remember { mutableStateOf(false) }
    var showCurrencyPicker by remember { mutableStateOf(false) }

    val today = LocalDate.now()
    val last30 = transactions.filter {
        runCatching { LocalDate.parse(it.date) }.getOrNull()?.let { txDate ->
            !txDate.isBefore(today.minusDays(30)) && !txDate.isAfter(today)
        } ?: false
    }
    val income30 = last30.filter { it.type == "Income" }.sumOf { it.amount.toDouble() }.toFloat()
    val expense30 = last30.filter { it.type == "Expense" }.sumOf { it.amount.toDouble() }.toFloat()

    Column(
        modifier = Modifier
            .background(Color(0xFFF5EEDC))
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, contentDescription = "Back") }
            Box {
                IconButton(onClick = { showMenu = true }) { Icon(Icons.Rounded.MoreVert, contentDescription = "More") }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Account settings") },
                        onClick = {
                            showMenu = false
                            onOpenAccountSettings()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Log out") },
                        onClick = {
                            showMenu = false
                            confirmLogout = true
                        }
                    )
                }
            }
        }

        Text("Arunav\nDas", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF27548A))
        Text("• OFFLINE ACCOUNT", style = MaterialTheme.typography.titleSmall, color = Color(0xFFDDA853), fontWeight = FontWeight.Bold)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Activity Summary", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF27548A), fontWeight = FontWeight.Bold)
            Text("Last 30 Days", style = MaterialTheme.typography.titleMedium, color = Color(0xFF183B4E).copy(alpha = 0.6f))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            SummaryCard("Income", income30, true, Modifier.weight(1f))
            SummaryCard("Expense", expense30, false, Modifier.weight(1f))
        }

        SectionLabel("GENERAL")
        ProfileListItem("Account Settings", Icons.Rounded.Settings, onOpenAccountSettings)
        ProfileListItem("Data Privacy", Icons.Rounded.Lock, subtitle = "LOCAL STORAGE ONLY") {
            Toast.makeText(context, "Your data is stored only on this device", Toast.LENGTH_SHORT).show()
        }
        ProfileListItem("Export Data", Icons.Rounded.Download) {
            val rows = mutableListOf("id,type,category,amount,date,notes")
            rows += transactions.map { "${it.id},${it.type},${it.category},${it.amount},${it.date},${it.notes}" }
            val path = viewModel.exportCsv(context, rows)
            Toast.makeText(context, "Exported: $path", Toast.LENGTH_LONG).show()
        }

        SectionLabel("PREFERENCES")
        ProfileListItem("Categories", Icons.Rounded.Category, onClick = onOpenCategories)
        ProfileListItem(
            title = "Currency Settings",
            icon = Icons.Rounded.Payments,
            trailingText = viewModel.currencyCode
        ) {
            showCurrencyPicker = true
        }

        OutlinedButton(
            onClick = { confirmLogout = true },
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(2.dp, Color(0xFF27548A)),
            shape = RoundedCornerShape(30.dp)
        ) {
            Icon(Icons.Rounded.Logout, contentDescription = null, tint = Color(0xFF27548A))
            Text(" Log Out", color = Color(0xFF27548A), style = MaterialTheme.typography.titleLarge)
        }

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("KHOROS v1.2.0", color = Color(0xFF183B4E).copy(alpha = 0.4f), style = MaterialTheme.typography.titleSmall)
            Text("Made with ♥ offline.", color = Color(0xFF183B4E).copy(alpha = 0.4f), style = MaterialTheme.typography.bodyMedium)
        }
    }

    if (showCurrencyPicker) {
        AlertDialog(
            onDismissRequest = { showCurrencyPicker = false },
            title = { Text("Currency Settings") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("INR (₹)" to "₹", "USD ($)" to "$", "EUR (€)" to "€").forEach { (label, value) ->
                        TextButton(onClick = {
                            viewModel.setCurrency(value)
                            showCurrencyPicker = false
                        }) { Text(label) }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyPicker = false }) { Text("Close") }
            }
        )
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
private fun SummaryCard(title: String, amount: Float, isIncome: Boolean, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(24.dp), tonalElevation = 1.dp, color = Color.White.copy(alpha = 0.7f)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(36.dp).background(Color(0xFFF5EEDC), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
                    Icon(
                        if (isIncome) Icons.Rounded.SouthWest else Icons.Rounded.NorthEast,
                        contentDescription = null,
                        tint = Color(0xFFDDA853)
                    )
                }
                Text(title, style = MaterialTheme.typography.titleLarge, color = Color(0xFF183B4E), fontWeight = FontWeight.Bold)
            }
            Text("₹${amount.toInt()}", style = MaterialTheme.typography.headlineMedium, color = Color(0xFF27548A), fontWeight = FontWeight.ExtraBold)
            Text(if (isIncome) "↗ Cash in" else "↘ Cash out", color = Color(0xFFDDA853), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.titleSmall, color = Color(0xFF27548A).copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
}

@Composable
private fun ProfileListItem(
    title: String,
    icon: ImageVector,
    subtitle: String? = null,
    trailingText: String? = null,
    onClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.size(48.dp).background(Color(0xFFF3E8CC), RoundedCornerShape(24.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = title, tint = Color(0xFFDDA853))
            }
            Column {
                Text(title, style = MaterialTheme.typography.titleLarge, color = Color(0xFF183B4E), fontWeight = FontWeight.Bold)
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.titleSmall, color = Color(0xFFDDA853), fontWeight = FontWeight.Bold)
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailingText != null) {
                Text(trailingText, color = Color(0xFF183B4E).copy(alpha = 0.5f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onClick) { Icon(Icons.Rounded.ChevronRight, contentDescription = title, tint = Color(0xFF183B4E).copy(alpha = 0.35f)) }
        }
    }
}
