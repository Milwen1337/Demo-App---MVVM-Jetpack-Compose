package com.milwen.wbpo_app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import com.milwen.wbpo_app.colorGrey
import com.milwen.wbpo_app.colorPrimaryDark
import com.milwen.wbpo_app.colorPrimaryLight
import com.milwen.wbpo_app.colorWhite

private val DarkColorScheme = darkColorScheme(
    primary = colorPrimaryDark,
    secondary = colorGrey,
    background = colorGrey
)

private val LightColorScheme = lightColorScheme(
    primary = colorPrimaryLight,
    secondary = colorGrey,
    background = colorWhite
)