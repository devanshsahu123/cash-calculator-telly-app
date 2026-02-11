package cash.money.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cash.money.calculator.data.PreferenceManager
import cash.money.calculator.navigation.AppNavigation
import cash.money.calculator.ui.theme.CashCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val prefManager = PreferenceManager(this)
        
        setContent {
            var appTheme by remember { mutableStateOf(prefManager.getAppTheme()) }
            
            CashCalculatorTheme(forceTheme = appTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        onThemeChange = { newTheme ->
                            prefManager.saveAppTheme(newTheme)
                            appTheme = newTheme
                        }
                    )
                }
            }
        }
    }
}
