package com.khoros.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    background = Background,
    surface = SurfaceCard,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onTertiary = Secondary,
    onBackground = Secondary,
    onSurface = Secondary
)

private val DarkColors = darkColorScheme(
    primary = Primary,
    secondary = Accent,
    tertiary = Accent,
    background = Secondary,
    surface = ColorTokens.darkSurface,
    onBackground = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color.White
)

private object ColorTokens {
    val darkSurface = androidx.compose.ui.graphics.Color(0xFF102733)
}

/**
 * Applies the Khoros design system to all composables.
 */
@Composable
fun KhorosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
