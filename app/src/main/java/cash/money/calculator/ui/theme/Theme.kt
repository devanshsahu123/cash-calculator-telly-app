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
    primary = SlateBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCFD8DC),
    onPrimaryContainer = DarkSlate,

    secondary = CoralAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0B2),
    onSecondaryContainer = Color(0xFFE64A19),

    tertiary = MintGreen,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB2DFDB),
    onTertiaryContainer = Color(0xFF00695C),

    background = SoftGrey,
    onBackground = DeepText,
    surface = Color.White,
    onSurface = DeepText,
    surfaceVariant = Color(0xFFECEFF1),
    onSurfaceVariant = Color(0xFF546E7A),

    error = ErrorOrange,
    onError = Color.White,
    errorContainer = Color(0xFFFFE0B2),
    onErrorContainer = Color(0xFFE64A19),

    outline = Color(0xFF90A4AE),
    outlineVariant = Color(0xFFB0BEC5),
    scrim = Color(0xFF000000)
)

// Dark Color Scheme
private val CashCalculatorDarkColors = darkColorScheme(
    primary = Color(0xFF90A4AE),
    onPrimary = DarkSlate,
    primaryContainer = SlateBlue,
    onPrimaryContainer = Color(0xFFCFD8DC),

    secondary = Color(0xFFFFAB91),
    onSecondary = Color(0xFFBF360C),
    secondaryContainer = CoralAccent,
    onSecondaryContainer = Color(0xFFFFE0B2),

    tertiary = Color(0xFF80CBC4),
    onTertiary = Color(0xFF004D40),
    tertiaryContainer = MintGreen,
    onTertiaryContainer = Color(0xFFB2DFDB),

    background = DarkSlate,
    onBackground = Color(0xFFECEFF1),
    surface = Color(0xFF263238),
    onSurface = Color(0xFFECEFF1),
    surfaceVariant = Color(0xFF37474F),
    onSurfaceVariant = Color(0xFFB0BEC5),

    error = Color(0xFFFFAB91),
    onError = Color(0xFFBF360C),
    errorContainer = ErrorOrange,
    onErrorContainer = Color(0xFFFFE0B2),

    outline = Color(0xFF78909C),
    outlineVariant = Color(0xFF546E7A),
    scrim = Color(0xFF000000)
)



@Composable
fun CashCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    forceTheme: String = "System",
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val isDark = when (forceTheme) {
        "Light" -> false
        "Dark" -> true
        else -> darkTheme
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDark) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        isDark -> CashCalculatorDarkColors
        else -> CashCalculatorLightColors
    }

    // Update system bars
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
