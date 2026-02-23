package com.khoros.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Defines bottom navigation destinations.
 */
sealed class NavDestination(val route: String, val title: String, val icon: ImageVector) {
    data object Dashboard : NavDestination("dashboard", "Dashboard", Icons.Rounded.Dashboard)
    data object Transactions : NavDestination("transactions", "Transactions", Icons.Rounded.ReceiptLong)
    data object Analytics : NavDestination("analytics", "Analytics", Icons.Rounded.Analytics)
    data object Settings : NavDestination("settings", "Settings", Icons.Rounded.Settings)
}

val bottomDestinations = listOf(
    NavDestination.Dashboard,
    NavDestination.Transactions,
    NavDestination.Analytics,
    NavDestination.Settings
)
