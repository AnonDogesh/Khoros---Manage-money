package com.khoros.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.khoros.app.data.local.KhorosDatabase
import com.khoros.app.data.repo.KhorosRepository
import com.khoros.app.ui.components.TransactionEditorDialog
import com.khoros.app.ui.navigation.NavDestination
import com.khoros.app.ui.navigation.bottomDestinations
import com.khoros.app.ui.screens.AnalyticsScreen
import com.khoros.app.ui.screens.DashboardScreen
import com.khoros.app.ui.screens.SettingsScreen
import com.khoros.app.ui.screens.TransactionsScreen
import com.khoros.app.ui.theme.KhorosTheme
import com.khoros.app.viewmodel.AnalyticsViewModel
import com.khoros.app.viewmodel.DashboardViewModel
import com.khoros.app.viewmodel.SettingsViewModel
import com.khoros.app.viewmodel.TransactionsViewModel
import com.khoros.app.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = KhorosDatabase.getInstance(this)
        val repository = KhorosRepository(db.transactionDao(), db.categoryDao())
        val factory = ViewModelFactory(repository)

        setContent {
            KhorosTheme {
                KhorosApp(factory)
            }
        }
    }
}

/**
 * Root composable containing bottom navigation and top-level destinations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KhorosApp(factory: ViewModelFactory) {
    val navController = rememberNavController()
    val dashboardVm: DashboardViewModel = viewModel(factory = factory)
    val txVm: TransactionsViewModel = viewModel(factory = factory)
    val analyticsVm: AnalyticsViewModel = viewModel(factory = factory)
    val settingsVm: SettingsViewModel = viewModel(factory = factory)
    var showEditor by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<com.khoros.app.data.model.TransactionEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { editing = null; showEditor = true }) {
                Icon(Icons.Rounded.Add, contentDescription = "Add transaction")
            }
        },
        bottomBar = {
            NavigationBar {
                val backstack by navController.currentBackStackEntryAsState()
                val route = backstack?.destination?.route
                bottomDestinations.forEach { item ->
                    NavigationBarItem(
                        selected = route == item.route,
                        onClick = { navController.navigate(item.route) },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = NavDestination.Dashboard.route, Modifier.padding(paddingValues)) {
            composable(NavDestination.Dashboard.route) { DashboardScreen(dashboardVm) }
            composable(NavDestination.Transactions.route) {
                TransactionsScreen(txVm, onEdit = { editing = it; showEditor = true })
            }
            composable(NavDestination.Analytics.route) { AnalyticsScreen(analyticsVm) }
            composable(NavDestination.Settings.route) { SettingsScreen(settingsVm) }
        }

        if (showEditor) {
            TransactionEditorDialog(
                categories = txVm.categories.value,
                existing = editing,
                onDismiss = { showEditor = false },
                onSave = {
                    if (editing == null) txVm.add(it) else txVm.update(it)
                    showEditor = false
                }
            )
        }
    }
}
