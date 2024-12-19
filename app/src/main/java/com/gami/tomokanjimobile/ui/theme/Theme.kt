package com.gami.tomokanji.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.gami.tomokanjimobile.ui.theme.ColorPalette
import com.gami.tomokanjimobile.ui.theme.CustomColorScheme

val LocalCustomColors = compositionLocalOf { ColorPalette }

@Composable
fun CustomTheme(
    colors: CustomColorScheme = ColorPalette,
    content: @Composable () -> Unit
) {
    val colorScheme = remember { colors }
    compositionLocalOf { colorScheme }

    content()
}

object CustomTheme {
    val colors: CustomColorScheme
        @Composable
        get() = LocalCustomColors.current
}