package com.example.newsapp.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = NewsBlue,
    onPrimary = LightSurface,

    background = LightBackground,
    onBackground = LightText,

    surface = LightSurface,
    onSurface = LightText,

    secondary = LightSecondaryText,
    onSecondary = LightSurface,

    outline = LightDivider,
    error = ErrorLight
)

private val DarkColorScheme = darkColorScheme(
    primary = NewsBlueDark,
    onPrimary = DarkBackground,

    background = DarkBackground,
    onBackground = DarkText,

    surface = DarkSurface,
    onSurface = DarkText,

    secondary = DarkSecondaryText,
    onSecondary = DarkBackground,

    outline = DarkDivider,
    error = ErrorDark
)

@Composable
fun NewsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}