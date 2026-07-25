package com.stotra.sahasranamam.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SaffronPrimary,
    secondary = SaffronSecondary,
    tertiary = GoldAccent,
    background = DeepBackgroundDark,
    surface = CardBackgroundDark,
    onPrimary = DeepBackgroundDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = SaffronSecondary,
    secondary = SaffronPrimary,
    tertiary = GoldAccent,
    background = Color.White,
    surface = Color(0xFFFFFFFF),
    onPrimary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF211A14),
    onSurface = Color(0xFF211A14)
)

@Composable
fun SahasranamamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
