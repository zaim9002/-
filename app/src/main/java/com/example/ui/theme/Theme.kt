package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = IslamicGreenLight,
    onPrimary = Color(0xFF042114),
    primaryContainer = IslamicGreenContainer,
    onPrimaryContainer = Color(0xFFB5ECD2),
    secondary = IslamicGold,
    onSecondary = Color(0xFF332400),
    secondaryContainer = Color(0xFF473708),
    onSecondaryContainer = IslamicGoldLight,
    tertiary = IslamicGreen,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = Color(0xFF16372D)
)

private val LightColorScheme = lightColorScheme(
    primary = IslamicGreen,
    onPrimary = Color.White,
    primaryContainer = IslamicGreenContainerLight,
    onPrimaryContainer = Color(0xFF073824),
    secondary = IslamicGoldDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFBF4E4),
    onSecondaryContainer = Color(0xFF473708),
    tertiary = IslamicGreenLight,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = Color(0xFFDFEBE4)
)

fun getAppColorScheme(darkTheme: Boolean, palette: String) = when (palette) {
    "teal" -> if (darkTheme) {
        darkColorScheme(
            primary = TealPrimaryLight,
            onPrimary = Color(0xFF003732),
            primaryContainer = TealContainerDark,
            onPrimaryContainer = Color(0xFFB2F5EA),
            secondary = IslamicGold,
            background = DarkBackground,
            surface = DarkSurface,
            surfaceVariant = DarkSurfaceVariant,
            outline = DarkBorder
        )
    } else {
        lightColorScheme(
            primary = TealPrimary,
            onPrimary = Color.White,
            primaryContainer = TealContainerLight,
            onPrimaryContainer = Color(0xFF003732),
            secondary = IslamicGoldDark,
            background = LightBackground,
            surface = LightSurface,
            surfaceVariant = LightSurfaceVariant,
            outline = LightBorder
        )
    }
    "navy" -> if (darkTheme) {
        darkColorScheme(
            primary = NavyPrimaryLight,
            onPrimary = Color(0xFF0F2942),
            primaryContainer = NavyContainerDark,
            onPrimaryContainer = Color(0xFFCCE4FF),
            secondary = IslamicGold,
            background = DarkBackground,
            surface = DarkSurface,
            surfaceVariant = DarkSurfaceVariant,
            outline = DarkBorder
        )
    } else {
        lightColorScheme(
            primary = NavyPrimary,
            onPrimary = Color.White,
            primaryContainer = NavyContainerLight,
            onPrimaryContainer = Color(0xFF0F2942),
            secondary = IslamicGoldDark,
            background = LightBackground,
            surface = LightSurface,
            surfaceVariant = LightSurfaceVariant,
            outline = LightBorder
        )
    }
    "amber" -> if (darkTheme) {
        darkColorScheme(
            primary = AmberPrimaryLight,
            onPrimary = Color(0xFF332000),
            primaryContainer = AmberContainerDark,
            onPrimaryContainer = Color(0xFFFFE082),
            secondary = IslamicGreenLight,
            background = DarkBackground,
            surface = DarkSurface,
            surfaceVariant = DarkSurfaceVariant,
            outline = DarkBorder
        )
    } else {
        lightColorScheme(
            primary = AmberPrimary,
            onPrimary = Color.White,
            primaryContainer = AmberContainerLight,
            onPrimaryContainer = Color(0xFF332000),
            secondary = IslamicGreen,
            background = LightBackground,
            surface = LightSurface,
            surfaceVariant = LightSurfaceVariant,
            outline = LightBorder
        )
    }
    else -> if (darkTheme) DarkColorScheme else LightColorScheme
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    palette: String = "emerald",
    content: @Composable () -> Unit,
) {
    val colorScheme = getAppColorScheme(darkTheme, palette)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
