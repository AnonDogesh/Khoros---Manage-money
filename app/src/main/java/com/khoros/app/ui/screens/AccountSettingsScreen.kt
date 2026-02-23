package com.khoros.app.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.khoros.app.viewmodel.TransactionsViewModel

@Composable
fun AccountSettingsScreen(
    transactionsViewModel: TransactionsViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("account_settings", Context.MODE_PRIVATE) }

    var fullName by remember { mutableStateOf(prefs.getString("full_name", "Arunav Das") ?: "Arunav Das") }
    var email by remember { mutableStateOf(prefs.getString("email", "arunav.das@example.com") ?: "arunav.das@example.com") }
    var pin by remember { mutableStateOf(prefs.getString("pin", "1234") ?: "1234") }
    var biometricEnabled by remember { mutableStateOf(prefs.getBoolean("biometric", false)) }

    var showNameDialog by remember { mutableStateOf(false) }
    var showEmailDialog by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(Color(0xFFF5EEDC))
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color(0xFF27548A)) }
            Text("Account Settings", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF27548A), fontWeight = FontWeight.ExtraBold)
        }

        SettingsSectionTitle("PERSONAL INFORMATION")
        SettingsCard {
            SettingsRow("NAME", fullName, Icons.Rounded.Badge, onClick = { showNameDialog = true })
            DividerLine()
            SettingsRow("EMAIL", email, Icons.Rounded.Email, onClick = { showEmailDialog = true })
        }

        SettingsSectionTitle("SECURITY")
        SettingsCard {
            SettingsRow("Change PIN", null, Icons.Rounded.Password, onClick = { showPinDialog = true })
            DividerLine()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Rounded.Fingerprint, contentDescription = null, tint = Color(0xFF27548A), modifier = Modifier.size(22.dp))
                    Text("Biometric Lock", style = MaterialTheme.typography.titleLarge, color = Color(0xFF183B4E))
                }
                Switch(
                    checked = biometricEnabled,
                    onCheckedChange = {
                        biometricEnabled = it
                        prefs.edit().putBoolean("biometric", it).apply()
                    }
                )
            }
        }

        SettingsSectionTitle("ACCOUNT MANAGEMENT")
        SettingsCard {
            SettingsRow("Reset Data", null, Icons.Rounded.RestartAlt, onClick = { showResetDialog = true })
            DividerLine()
            SettingsRow("Delete Account", null, Icons.Rounded.Delete, highlight = Color(0xFFE53935), onClick = { showDeleteDialog = true })
        }

        Text(
            "Deleting your account will permanently remove all offline data stored on this device. This action cannot be undone.",
            color = Color(0xFF183B4E).copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyLarge
        )

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("khoros v1.2.0 (stable)", color = Color(0xFF183B4E).copy(alpha = 0.35f), style = MaterialTheme.typography.titleMedium)
            Text("🛡 SECURE & OFFLINE", color = Color(0xFFDDA853), style = MaterialTheme.typography.bodyMedium)
        }
    }

    if (showNameDialog) {
        EditFieldDialog(
            title = "Edit Name",
            value = fullName,
            onDismiss = { showNameDialog = false },
            onSave = {
                fullName = it
                prefs.edit().putString("full_name", it).apply()
                showNameDialog = false
            }
        )
    }

    if (showEmailDialog) {
        EditFieldDialog(
            title = "Edit Email",
            value = email,
            onDismiss = { showEmailDialog = false },
            onSave = {
                email = it
                prefs.edit().putString("email", it).apply()
                showEmailDialog = false
            }
        )
    }

    if (showPinDialog) {
        EditFieldDialog(
            title = "Change PIN",
            value = pin,
            onDismiss = { showPinDialog = false },
            onSave = {
                pin = it.filter(Char::isDigit).take(6)
                prefs.edit().putString("pin", pin).apply()
                showPinDialog = false
                Toast.makeText(context, "PIN updated", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset all data?") },
            text = { Text("This will clear transactions, custom categories and budget limits, then restore defaults.") },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    transactionsViewModel.resetOfflineData()
                    context.getSharedPreferences("budget_limits", Context.MODE_PRIVATE).edit().clear().apply()
                    Toast.makeText(context, "Data reset complete", Toast.LENGTH_SHORT).show()
                }) { Text("Reset") }
            },
            dismissButton = { TextButton(onClick = { showResetDialog = false }) { Text("Cancel") } }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete account?") },
            text = { Text("All offline account data and transactions will be permanently removed from this device.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    transactionsViewModel.deleteOfflineAccountData()
                    context.getSharedPreferences("budget_limits", Context.MODE_PRIVATE).edit().clear().apply()
                    prefs.edit().clear().apply()
                    Toast.makeText(context, "Offline account deleted", Toast.LENGTH_LONG).show()
                    onBack()
                }) { Text("Delete", color = Color(0xFFE53935)) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, color = Color(0xFF183B4E), fontWeight = FontWeight.ExtraBold)
}

@Composable
private fun SettingsCard(content: @Composable Column.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F8F8), RoundedCornerShape(22.dp))
            .padding(vertical = 2.dp),
        content = content
    )
}

@Composable
private fun SettingsRow(
    label: String,
    value: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    highlight: Color = Color(0xFF183B4E),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, contentDescription = null, tint = highlight, modifier = Modifier.size(22.dp))
            Column {
                Text(label, color = if (value == null) highlight else Color(0xFF183B4E).copy(alpha = 0.65f), style = MaterialTheme.typography.bodyLarge)
                if (value != null) Text(value, color = Color(0xFF183B4E), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            }
        }
        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = if (highlight == Color(0xFFE53935)) Color(0xFFE53935).copy(alpha = 0.5f) else Color(0xFF183B4E).copy(alpha = 0.3f))
    }
}

@Composable
private fun DividerLine() {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        androidx.compose.material3.HorizontalDivider(color = Color(0xFF183B4E).copy(alpha = 0.08f))
    }
}

@Composable
private fun EditFieldDialog(
    title: String,
    value: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember(value) { mutableStateOf(value) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            androidx.compose.material3.OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { if (text.isNotBlank()) onSave(text.trim()) }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
