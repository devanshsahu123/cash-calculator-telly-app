package cash.money.calculator.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cash.money.calculator.ui.screens.CashCalculatorScreen
import cash.money.calculator.ui.screens.HistoryScreen

// Define routes
object AppRoutes {
    const val CALCULATOR = "calculator"
    const val HISTORY = "history"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppRoutes.CALCULATOR
    ) {
        // Calculator Screen
        composable(AppRoutes.CALCULATOR) {
            CashCalculatorScreen(
                onNavigateToHistory = {
                    navController.navigate(AppRoutes.HISTORY)
                }
            )
        }

        // History Screen
        composable(AppRoutes.HISTORY) {
            HistoryScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}
