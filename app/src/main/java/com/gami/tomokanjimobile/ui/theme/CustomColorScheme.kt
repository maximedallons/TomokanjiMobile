package com.gami.tomokanjimobile.ui.theme

import androidx.compose.ui.graphics.Color

data class CustomColorScheme(
    val primary : Color,
    val secondary : Color,
    val tertiary : Color,
    val textPrimary : Color,
    val textSecondary : Color,
    val backgroundPrimary : Color,
    val backgroundSecondary : Color,
    val backgroundTertiary : Color,
    val semanticSuccess : Color,
    val semanticError : Color,
    val semanticWarning : Color,
    val semanticInfo : Color
)

val ColorPalette = CustomColorScheme(
    primary = Color(0xFFBB9C62),
    secondary = Color(0xFFCCB07F),
    tertiary = Color(0xFFEE8B60),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFF939393),
    backgroundPrimary = Color(0xFF010101),
    backgroundSecondary = Color(0xFF151515),
    backgroundTertiary = Color(0xFF4c4c4c),
    semanticSuccess = Color(0xFF249689),
    semanticError = Color(0xFFFF5963),
    semanticWarning = Color(0xFFF9CF58),
    semanticInfo = Color(0xFF4E9AF1)
)