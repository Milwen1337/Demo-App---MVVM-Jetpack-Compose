package com.milwen.wbpo_app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.milwen.wbpo_app.*

private val DarkColorScheme = darkColorScheme(
    primary = colorPrimaryDark,
    secondary = colorGrey,
    tertiary = colorLightGrey,
    background = colorGrey,
    error = colorDeclineLight
)

private val LightColorScheme = lightColorScheme(
    primary = colorPrimaryLight,
    secondary = colorGrey,
    tertiary = colorPrimaryDark,
    background = colorWhite,
    error = colorDecline
)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colors, content = content)
}