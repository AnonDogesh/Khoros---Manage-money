package com.khoros.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Defines app navigation destinations.
 */
sealed class NavDestination(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Home : NavDestination("home", "Home", Icons.Rounded.Home)
    data object Analytics : NavDestination("analytics", "Analytics", Icons.Rounded.Analytics)
    data object Budget : NavDestination("budget", "Budget", Icons.Rounded.AccountBalanceWallet)
    data object Profile : NavDestination("profile", "Profile", Icons.Rounded.Person)
    data object Transactions : NavDestination("transactions", "Transactions")
    data object AccountSettings : NavDestination("account_settings", "Account Settings")
    data object AddTransaction : NavDestination("add_transaction?transactionId={transactionId}", "Add")
}

val bottomDestinations = listOf(
    NavDestination.Home,
    NavDestination.Analytics,
    NavDestination.Budget,
    NavDestination.Profile
)
