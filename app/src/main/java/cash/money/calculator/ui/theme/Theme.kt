package cash.money.calculator.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light Color Scheme
private val CashCalculatorLightColors = lightColorScheme(
    primary = RoyalIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8EAF6),
    onPrimaryContainer = DeepIndigo,

    secondary = EmeraldGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E9),
    onSecondaryContainer = Color(0xFF1B5E20),

    tertiary = GoldAccent,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFFFF9C4),
    onTertiaryContainer = Color(0xFFF57F17),

    background = SoftBackground,
    onBackground = Color(0xFF1C1B1F),
    surface = SurfaceWhite,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFECEFF1),
    onSurfaceVariant = Color(0xFF455A64),

    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C),

    outline = Color(0xFF78909C),
    outlineVariant = Color(0xFFCFD8DC),
    scrim = Color(0xFF000000)
)

// Dark Color Scheme
private val CashCalculatorDarkColors = darkColorScheme(
    primary = Color(0xFFC5CAE9),
    onPrimary = DeepIndigo,
    primaryContainer = RoyalIndigo,
    onPrimaryContainer = Color(0xFFE8EAF6),

    secondary = Color(0xFFA5D6A7),
    onSecondary = Color(0xFF1B5E20),
    secondaryContainer = EmeraldGreen,
    onSecondaryContainer = Color(0xFFE8F5E9),

    tertiary = GoldAccent,
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFF57F17),
    onTertiaryContainer = Color(0xFFFFF9C4),

    background = Color(0xFF121212),
    onBackground = Color(0xFFE1E2E1),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE1E2E1),
    surfaceVariant = Color(0xFF263238),
    onSurfaceVariant = Color(0xFFB0BEC5),

    error = Color(0xFFEF9A9A),
    onError = Color(0xFFB71C1C),
    errorContainer = ErrorRed,
    onErrorContainer = Color(0xFFFFEBEE),

    outline = Color(0xFF90A4AE),
    outlineVariant = Color(0xFF455A64),
    scrim = Color(0xFF000000)
)


@Composable
fun CashCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> CashCalculatorDarkColors
        else -> CashCalculatorLightColors
    }

    // Update system bars
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
