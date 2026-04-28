package com.synq.app.core.theme
import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = TealAccent, secondary = DeepBlueLight, tertiary = TealAccentDark,
    background = DarkBackground, surface = DarkSurface,
    onPrimary = TextWhite, onSecondary = TextWhite, onTertiary = TextWhite,
    onBackground = TextWhite, onSurface = TextWhite,
    surfaceVariant = DarkSurfaceElevated, onSurfaceVariant = TextGray
)

@Composable
fun SynqTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkColorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(colorScheme = DarkColorScheme, content = content)
}
