package com.raye.brewkery.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TerracottaPrimary,
    secondary = EspressoDark,
    tertiary = StarYellow,
    background = EspressoDark,
    surface = EspressoDark,
    onPrimary = CardSurface,
    onBackground = CreamBackground,
    onSurface = CreamBackground
)

private val LightColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    secondary = EspressoDark,
    tertiary = StarYellow,
    background = CreamBackground,
    surface = CardSurface,
    onPrimary = CardSurface,
    onSecondary = CardSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun BrewkeryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}