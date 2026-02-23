package com.khoros.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.khoros.app.data.local.KhorosDatabase
import com.khoros.app.data.repo.KhorosRepository
import com.khoros.app.ui.navigation.NavDestination
import com.khoros.app.ui.navigation.bottomDestinations
import com.khoros.app.ui.screens.AccountSettingsScreen
import com.khoros.app.ui.screens.AddTransactionScreen
import com.khoros.app.ui.screens.AnalyticsScreen
import com.khoros.app.ui.screens.BudgetScreen
import com.khoros.app.ui.screens.DashboardScreen
import com.khoros.app.ui.screens.ProfileScreen
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
@Composable
fun KhorosApp(factory: ViewModelFactory) {
    val navController = rememberNavController()
    val dashboardVm: DashboardViewModel = viewModel(factory = factory)
    val txVm: TransactionsViewModel = viewModel(factory = factory)
    val analyticsVm: AnalyticsViewModel = viewModel(factory = factory)
    val settingsVm: SettingsViewModel = viewModel(factory = factory)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val tx by txVm.transactions.collectAsState()
    var budgetAddRequest by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (currentRoute !in listOf(NavDestination.Profile.route, NavDestination.AccountSettings.route)) {
                FloatingActionButton(onClick = {
                    if (currentRoute == NavDestination.Budget.route) {
                        budgetAddRequest = true
                    } else {
                        navController.navigate("add_transaction")
                    }
                }) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add transaction")
                }
            }
        },
        bottomBar = {
            if (currentRoute?.startsWith("add_transaction") != true && currentRoute != NavDestination.AccountSettings.route) {
                NavigationBar {
                    val route = navController.currentBackStackEntryAsState().value?.destination?.route
                    bottomDestinations.forEach { item ->
                        NavigationBarItem(
                            selected = route == item.route,
                            onClick = { navController.navigate(item.route) },
                            icon = { Icon(item.icon!!, contentDescription = item.title) },
                            label = { Text(item.title) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(navController = navController, startDestination = NavDestination.Home.route, Modifier.padding(paddingValues)) {
            composable(NavDestination.Home.route) {
                DashboardScreen(
                    viewModel = dashboardVm,
                    onSeeAllTransactions = { navController.navigate(NavDestination.Transactions.route) }
                )
            }
            composable(NavDestination.Transactions.route) {
                TransactionsScreen(
                    viewModel = txVm,
                    onEdit = { navController.navigate("add_transaction?transactionId=${it.id}") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(NavDestination.Analytics.route) { AnalyticsScreen(analyticsVm) }
            composable(NavDestination.Budget.route) {
                BudgetScreen(
                    viewModel = txVm,
                    requestAddCategory = budgetAddRequest,
                    onAddCategoryConsumed = { budgetAddRequest = false }
                )
            }
            composable(NavDestination.Profile.route) {
                ProfileScreen(
                    viewModel = settingsVm,
                    transactions = tx,
                    onOpenAccountSettings = { navController.navigate(NavDestination.AccountSettings.route) },
                    onOpenCategories = { navController.navigate(NavDestination.Budget.route) },
                    onBack = { navController.navigate(NavDestination.Home.route) }
                )
            }
            composable(NavDestination.AccountSettings.route) {
                AccountSettingsScreen(
                    transactionsViewModel = txVm,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = NavDestination.AddTransaction.route,
                arguments = listOf(navArgument("transactionId") { type = NavType.StringType; nullable = true; defaultValue = null })
            ) { backStack ->
                val id = backStack.arguments?.getString("transactionId")?.toIntOrNull()
                AddTransactionScreen(
                    viewModel = txVm,
                    existing = id?.let { txVm.findById(it) },
                    onCancel = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
        }
    }
}
